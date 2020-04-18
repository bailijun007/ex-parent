package com.hp.sh.expv3.pc.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.grab3rdData.pojo.*;
import com.hp.sh.expv3.pc.grab3rdData.service.SupportBbGroupIdsJobService;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import com.hp.sh.expv3.config.redis.RedisUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author BaiLiJun  on 2020/4/10
 */
@Component
public class GetLastPriceByMerge {

    private static final Logger logger = LoggerFactory.getLogger(GetLastPriceByMerge.class);

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            1,
            1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    @Value("${zb.wss.url}")
    private String zbWssUrl;

    @Value("${zb.https.url}")
    private String zbHttpsUrl;

    @Value("${zb.wss.redisKey.prefix}")
    private String zbWssRedisKey;

    @Value("${binance.wss.redisKey.prefix}")
    private String binanceWssRedisKey;

    @Value("${zb.https.redisKey.prefix}")
    private String zbHttpsRedisKey;

    @Value("${binance.https.redisKey.prefix}")
    private String binanceHttpsRedisKey;

    @Value("${ok.https.redisKey.prefix}")
    private String okHttpsRedisKey;

    @Value("${bitfinex.https.redisKey.prefix}")
    private String bitfinexHttpsRedisKey;

    @Autowired
    @Qualifier("originaldataDb5RedisUtil")
    private RedisUtil originaldataDb5RedisUtil;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;

    @Value("${grab.pc.3rdDataByZbWss.enable}")
    private Integer enableByWss;

    @Value("${grab.pc.3rdDataByZbHttps.enable}")
    private Integer enableByHttps;

    @Value("${ws.redisKey.prefix}")
    private String wsRedisKeyPrefix;

    @Value("${https.redisKey.prefix}")
    private String httpsRedisKeyPrefix;

    @Value("${zb.name.prefix}")
    private String zbPrefix;

    @Value("${ok.name.prefix}")
    private String okPrefix;

    @Value("${bitfinex.name.prefix}")
    private String bitfinexPrefix;

    @Value("${binance.name.prefix}")
    private String binancePrefix;

    @Autowired
    private SupportBbGroupIdsJobService supportBbGroupIdsJobService;


    @Scheduled(cron = "*/1 * * * * *")
    public void merge() {
        List<PcSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            Map<String, String> symbolsMap = new HashMap<>();
            for (PcSymbol pcSymbol : bbSymbolList) {
                String key = "ticker:pc:lastPrice:" + pcSymbol.getAsset();
                List<String> symbols = new ArrayList<>();
                symbols.add(pcSymbol.getSymbol());
                symbolsMap.put(pcSymbol.getSymbol(), pcSymbol.getSymbol());
                Map<String, String> lastPriceMap = metadataDb5RedisUtil.hmget(key, symbols);
                Map<String, BigDecimal> currentPriceMap = mergeByAvg(pcSymbol);
                BigDecimal avgPrice = this.filter(lastPriceMap, currentPriceMap, symbolsMap);
                if (avgPrice.compareTo(BigDecimal.ZERO) != 0) {
                    saveMerge(pcSymbol, avgPrice, key);
                }
                lastPriceMap.clear();
            }
        }
    }

    private BigDecimal filter(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, Map<String, String> symbolsMap) {
        BigDecimal avgPrice = BigDecimal.ZERO;
        BigDecimal sumPrice = BigDecimal.ZERO;
        if (currentPriceMap.size() >= 3) {
            return getAvgPriceByMergeMoreThan3Bourse(lastPriceMap, currentPriceMap, avgPrice, sumPrice, symbolsMap);
        }

        if (currentPriceMap.size() == 2) {
            return getAvgPriceByMergeMoreThan2Bourse(lastPriceMap, currentPriceMap, avgPrice, sumPrice,symbolsMap);
        }

        if (currentPriceMap.size() == 1) {
            return getAvgPriceByMerge1Bourse(lastPriceMap, currentPriceMap, symbolsMap);
        }
        return avgPrice;
    }


    /**
     * 通过合并2个交易所获取最新平均成交价
     *
     * @param currentPriceMap
     * @param avgPrice
     * @param sumPrice
     * @return
     */
    private BigDecimal getAvgPriceByMergeMoreThan2Bourse(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, BigDecimal avgPrice, BigDecimal sumPrice, Map<String, String> symbolsMap) {
        List<BigDecimal> currentPriceList = new ArrayList<>(currentPriceMap.values());
        BigDecimal medianPrice = generatedMedian(currentPriceList);
        if (medianPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        for (String s : currentPriceMap.keySet()) {
            BigDecimal price = currentPriceMap.get(s);
            BigDecimal rule = (price.subtract(medianPrice).abs()).divide(medianPrice, 4);
            if (rule.compareTo(new BigDecimal("0.125")) == 1) {
                currentPriceMap.remove(s);
                filter(lastPriceMap, currentPriceMap,symbolsMap);
            }
        }
        if (CollectionUtils.isEmpty(currentPriceMap)) {
            return BigDecimal.ZERO;
        } else {
            for (String s : currentPriceMap.keySet()) {
                BigDecimal bigDecimal = currentPriceMap.get(s);
                sumPrice = sumPrice.add(bigDecimal);
            }
            avgPrice = sumPrice.divide(new BigDecimal(currentPriceMap.size()), 4, RoundingMode.DOWN);
        }

        return avgPrice;
    }

    /**
     * 通过合并1个交易所获取最新成交价
     *
     * @param lastPriceMap
     * @param currentPriceMap
     * @return
     */
    private BigDecimal getAvgPriceByMerge1Bourse(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, Map<String, String> symbolsMap) {
        BigDecimal currentPrice = new ArrayList<>(currentPriceMap.values()).get(0);
        BigDecimal lastPrice = BigDecimal.ZERO;
        //如果没有最新成交价，则直接用当前获取的成交价
        if (CollectionUtils.isEmpty(lastPriceMap)) {
            return currentPrice;
        } else if (CollectionUtils.isEmpty(currentPriceMap)) {
            return BigDecimal.ZERO;
        } else {
            for (String s : lastPriceMap.keySet()) {
                if (symbolsMap.containsKey(s)) {
                    String lastPriceStr = lastPriceMap.get(s);
                    lastPrice = new BigDecimal(lastPriceStr);
                }
            }
        }

        return currentPrice;
//        if (currentPrice.subtract(lastPrice).abs().compareTo(new BigDecimal("0.25")) == 1) {
//            return BigDecimal.ZERO;
//        } else {
//            return currentPrice;
//        }
    }

    /**
     * 通过合并3个以上交易所得到平均价
     *
     * @param currentPriceMap
     * @param avgPrice
     * @param sumPrice
     * @return
     */
    private BigDecimal getAvgPriceByMergeMoreThan3Bourse(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, BigDecimal avgPrice, BigDecimal sumPrice, Map<String, String> symbolsMap) {
        List<BigDecimal> currentPriceList = new ArrayList<>(currentPriceMap.values());
        BigDecimal medianPrice = generatedMedian(currentPriceList);
        if (medianPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        for (String s : currentPriceMap.keySet()) {
            BigDecimal price = currentPriceMap.get(s);
            BigDecimal abs = price.subtract(medianPrice).abs();
            BigDecimal rule = abs.divide(medianPrice, 4, RoundingMode.DOWN);
            if (rule.compareTo(new BigDecimal("0.25")) == 1) {
                currentPriceMap.remove(s);
                filter(lastPriceMap, currentPriceMap,symbolsMap);
            }
        }
        if (CollectionUtils.isEmpty(currentPriceMap)) {
            return avgPrice;
        } else {
            //求平均价
            for (String s : currentPriceMap.keySet()) {
                sumPrice = sumPrice.add(currentPriceMap.get(s));
            }
            avgPrice = sumPrice.divide(new BigDecimal(currentPriceMap.size()), 4, RoundingMode.DOWN);
            return avgPrice;
        }
    }


    /**
     * 生成中位数
     *
     * @param list
     * @return
     */
    private BigDecimal generatedMedian(List<BigDecimal> list) {
        // 生成中位数
        BigDecimal j;
        if (list.size() % 2 == 0) {
            j = (list.get(list.size() / 2 - 1).add(list.get(list.size() / 2))).divide(new BigDecimal("2"), 4, RoundingMode.DOWN);
        } else {
            j = list.get(list.size() / 2);
        }
        return j;
    }

    private Map<String, BigDecimal> mergeByAvg(PcSymbol pcSymbol) {
        Map<String, BigDecimal> currentPriceMap = new ConcurrentHashMap<>();

//        BigDecimal binanceAvgPrice = mergeByBinance(pcSymbol);
//        if (binanceAvgPrice.compareTo(BigDecimal.ZERO) != 0) {
//            currentPriceMap.put("binance", binanceAvgPrice);
//            logger.info("binance最新成交均价为:{},", binanceAvgPrice);
//
//        }

        BigDecimal okAvgPrice = mergeByOk(pcSymbol);
        if (okAvgPrice.compareTo(BigDecimal.ZERO) != 0) {
            currentPriceMap.put("ok", okAvgPrice);
            logger.info("ok最新成交均价为:{},", okAvgPrice);
        }
        return currentPriceMap;
    }


    private void saveMerge(PcSymbol bbSymbol, BigDecimal avgLastPrice, String key) {
        HashMap<String, BigDecimal> map = new HashMap<>();
        map.put(bbSymbol.getSymbol(), avgLastPrice);
        metadataDb5RedisUtil.hmset(key, map);
        originaldataDb5RedisUtil.hmset(key, map);
    }

    public BigDecimal mergeByBinance(PcSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
        String wssKey = binanceWssRedisKey + hashKey;
        String strWssTicker = metadataDb5RedisUtil.get(wssKey);
//        BinanceResponseData wssTicker = JSON.parseObject(strWssTicker, BinanceResponseData.class);
        BigDecimal wssLast = BigDecimal.ZERO;
        if (null == strWssTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else {
            avgLastPrice = new BigDecimal(strWssTicker);
        }

        logger.info("binance的交易对:{},wssLast的最新成交价为：{}", hashKey, wssLast);
        logger.info("binance的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }


    public BigDecimal mergeByOk(PcSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + "-" + symbol.split("_")[1];
        String httpsKey = okHttpsRedisKey + hashKey;
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
//        OkResponseEntity httpsTicker = JSON.parseObject(strHttpsTicker, OkResponseEntity.class);
        if (null == strHttpsTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else {
            avgLastPrice = new BigDecimal(strHttpsTicker);
        }
        logger.info("ok的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }

}
