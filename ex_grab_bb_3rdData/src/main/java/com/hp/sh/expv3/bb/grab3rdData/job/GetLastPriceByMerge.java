package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hp.sh.expv3.bb.grab3rdData.pojo.*;
import com.hp.sh.expv3.bb.grab3rdData.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Value("${bb.trade.symbols}")
    private String symbols;

    @Value("${bb.trade.bbGroupIds}")
    private Integer bbGroupId;

//    @Autowired
//    @Qualifier("originaldataDb5RedisUtil")
//    private RedisUtil originaldataDb5RedisUtil;

    @Autowired
    @Qualifier("metadataDb5RedisUtil")
    private RedisUtil metadataDb5RedisUtil;

    @Value("${grab.bb.3rdDataByZbWss.enable}")
    private Integer enableByWss;

    @Value("${grab.bb.3rdDataByZbHttps.enable}")
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
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        BBSymbol bchSymbol = new BBSymbol();
        bchSymbol.setSymbol("BCHABC_USDT");
        bbSymbolList.add(bchSymbol);
        Map<String, String> symbolMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                List<String> symbols = new ArrayList<>();
                String key = "ticker:bb:lastPrice:" + bbSymbol.getAsset();
                symbols.add(bbSymbol.getSymbol());
                symbolMap.put(bbSymbol.getSymbol(), bbSymbol.getSymbol());
                Map<String, String> lastPriceMap = metadataDb5RedisUtil.hmget(key, symbols);
                Map<String, BigDecimal> currentPriceMap = mergeByAvg(bbSymbol);
                BigDecimal avgPrice = this.filter(lastPriceMap, currentPriceMap, symbolMap);
                if (avgPrice.compareTo(BigDecimal.ZERO) != 0) {
                    saveMerge(bbSymbol, avgPrice, key);
                }
            }
        }
    }

    private BigDecimal filter(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, Map<String, String> symbolMap) {
        BigDecimal avgPrice = BigDecimal.ZERO;
        BigDecimal sumPrice = BigDecimal.ZERO;
        if (currentPriceMap.size() >= 3) {
            return getAvgPriceByMergeMoreThan3Bourse(lastPriceMap, currentPriceMap, avgPrice, sumPrice, symbolMap);
        }

        if (currentPriceMap.size() == 2) {
            return  getAvgPriceByMergeMoreThan2Bourse(lastPriceMap, currentPriceMap, avgPrice, sumPrice, symbolMap);
        }

        if (currentPriceMap.size() == 1) {
            return getAvgPriceByMerge1Bourse(lastPriceMap, currentPriceMap, symbolMap);
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
    private BigDecimal getAvgPriceByMerge1Bourse(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, Map<String, String> symbolMap) {
        BigDecimal currentPrice = new ArrayList<>(currentPriceMap.values()).get(0);
        BigDecimal lastPrice = BigDecimal.ZERO;
        //如果没有最新成交价，则直接用当前获取的成交价
        if (CollectionUtils.isEmpty(lastPriceMap)) {
            return currentPrice;
        } else if (CollectionUtils.isEmpty(currentPriceMap)) {
            return BigDecimal.ZERO;
        } else {
//                String lastPriceStr = new ArrayList<>((List<String>) lastPriceMap.values()).get(0);
            for (String s : lastPriceMap.keySet()) {
                if (symbolMap.containsKey(s)) {
                    String lastPriceStr = lastPriceMap.get(s);
                    lastPrice = new BigDecimal(lastPriceStr);
                }
            }
        }

        if (currentPrice.subtract(lastPrice).abs().compareTo(new BigDecimal("0.25")) == 1) {
            return BigDecimal.ZERO;
        } else {
            return currentPrice;
        }
    }

    /**
     * 通过合并2个交易所获取最新平均成交价
     *
     * @param currentPriceMap
     * @param avgPrice
     * @param sumPrice
     * @return
     */
    private BigDecimal getAvgPriceByMergeMoreThan2Bourse(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, BigDecimal avgPrice, BigDecimal sumPrice, Map<String, String> symbolMap) {
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
                filter(lastPriceMap, currentPriceMap, symbolMap);
            }
        }
        if (CollectionUtils.isEmpty(currentPriceMap)) {
            return BigDecimal.ZERO;
        }
        //如果过滤后还是currentPriceMap.size() == 2,则直接算出均值，return
        if (currentPriceMap.size() == 2) {
            for (String s : currentPriceMap.keySet()) {
                BigDecimal bigDecimal = currentPriceMap.get(s);
                sumPrice = sumPrice.add(bigDecimal);
            }
            avgPrice = sumPrice.divide(new BigDecimal(currentPriceMap.size()), 4, RoundingMode.DOWN);
        }
        return avgPrice;
    }

    /**
     * 通过合并3个以上交易所得到平均价
     *
     * @param currentPriceMap
     * @param avgPrice
     * @param sumPrice
     * @return
     */
    private BigDecimal getAvgPriceByMergeMoreThan3Bourse(Map<String, String> lastPriceMap, Map<String, BigDecimal> currentPriceMap, BigDecimal avgPrice, BigDecimal sumPrice, Map<String, String> symbolMap) {
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
                filter(lastPriceMap, currentPriceMap, symbolMap);
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

    private Map<String, BigDecimal> mergeByAvg(BBSymbol bbSymbol) {
        Map<String, BigDecimal> currentPriceMap = new ConcurrentHashMap<>();
        BigDecimal zbAvgPrice = mergeByZb(bbSymbol);
        if (zbAvgPrice.compareTo(BigDecimal.ZERO) != 0) {
            currentPriceMap.put("zb", zbAvgPrice);
            logger.info("zb最新成交均价为:{},", zbAvgPrice);
        }

        BigDecimal binanceAvgPrice = mergeByBinance(bbSymbol);
        if (binanceAvgPrice.compareTo(BigDecimal.ZERO) != 0) {
            currentPriceMap.put("binance", binanceAvgPrice);
            logger.info("binance最新成交均价为:{},", binanceAvgPrice);
        }

        BigDecimal bitfinexAvgPrice = mergeByBitfinex(bbSymbol);
        if (bitfinexAvgPrice.compareTo(BigDecimal.ZERO) != 0) {
            currentPriceMap.put("bitfinex", bitfinexAvgPrice);
            logger.info("bitfinex最新成交均价为:{},", bitfinexAvgPrice);
        }

        BigDecimal okAvgPrice = mergeByOk(bbSymbol);
        if (okAvgPrice.compareTo(BigDecimal.ZERO) != 0) {
            currentPriceMap.put("ok", okAvgPrice);
            logger.info("ok最新成交均价为:{},", okAvgPrice);
        }

        return currentPriceMap;
    }


    public BigDecimal mergeByZb(BBSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol().toLowerCase();
        String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
        String httpsKey = zbHttpsRedisKey + hashKey;
        if(httpsKey.equals("ticker:bb:https:zb:bchusdt")){
            httpsKey="ticker:bb:https:zb:bchabcusdt";
        }
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);

        String wssKey = zbWssRedisKey + hashKey;
        if(wssKey.equals("ticker:bb:wss:zb:bchusdt")){
            wssKey="ticker:bb:wss:zb:bchabcusdt";
        }
        String strWssTicker = metadataDb5RedisUtil.get(wssKey);
        BigDecimal httpLast = BigDecimal.ZERO;
        BigDecimal wssLast = BigDecimal.ZERO;
        if (null == strHttpsTicker && null == strWssTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else if (null != strHttpsTicker && null != strWssTicker) {
            httpLast = new BigDecimal(strHttpsTicker.split("&")[0]);
            wssLast = new BigDecimal(strWssTicker.split("&")[0]);
            avgLastPrice = httpLast.add(wssLast).divide(new BigDecimal(2), 4, RoundingMode.DOWN);
        }

        if (null == strHttpsTicker && null != strWssTicker) {
            avgLastPrice = new BigDecimal(strWssTicker.split("&")[0]);
        } else if (null == strWssTicker && null != strHttpsTicker) {
            avgLastPrice = new BigDecimal(strHttpsTicker.split("&")[0]);
        }

        logger.info("zb的交易对:{},httpLast的最新成交价为：{},wssLast的最新成交价为：{}", hashKey, httpLast, wssLast);
        logger.info("zb的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);

        return avgLastPrice;
    }


    private void saveMerge(BBSymbol bbSymbol, BigDecimal avgLastPrice, String key) {
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String format = dateTime.format(dtf);
        HashMap<String, BigDecimal> map = new HashMap<>();
        List<String> list = new ArrayList<>();
        String symbol = bbSymbol.getSymbol();
        if(symbol.equals("BCHABC_USDT")){
            key="USDT";
            symbol ="BCH_USDT";
        }
        map.put(symbol, avgLastPrice);
        metadataDb5RedisUtil.hmset(key, map);
//        originaldataDb5RedisUtil.hmset(key, map);
//        metadataDb5RedisUtil.zadd("his:" + key, new HashMap<String, Double>() {{
//            put(bbSymbol.getSymbol(), Double.valueOf(format));
//        }});

        list.add(symbol);
        if (symbol.equals("ETH_USDT")) {
             BigDecimal ethUsdtValue = map.get("ETH_USDT");
            BigDecimal bysValue =ethUsdtValue.divide(new BigDecimal("7000"), 8, RoundingMode.DOWN);
            map.clear();
            map.put("BYS_USDT",bysValue);
            metadataDb5RedisUtil.hmset(key, map);
//            originaldataDb5RedisUtil.hmset(key, map);
        }

        logger.info("当前时间={},asset={},symbol={},最终最新成交均价为:{},", format, bbSymbol.getAsset(), bbSymbol.getSymbol(), avgLastPrice);

    }

    public BigDecimal mergeByBinance(BBSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
        String httpsKey = binanceHttpsRedisKey + hashKey;
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
//        Map httpsTicker = JSON.parseObject(strHttpsTicker, Map.class);

        String wssKey = binanceWssRedisKey + hashKey;
        String strWssTicker = metadataDb5RedisUtil.get(wssKey);
//        BinanceResponseData wssTicker = JSON.parseObject(strWssTicker, BinanceResponseData.class);
        BigDecimal httpLast = BigDecimal.ZERO;
        BigDecimal wssLast = BigDecimal.ZERO;
        if (null == strHttpsTicker && null == strWssTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else if (null != strHttpsTicker && null != strWssTicker) {
//            Object price = httpsTicker.get("price");
            httpLast = new BigDecimal(strHttpsTicker.split("&")[0]);
            wssLast = new BigDecimal(strWssTicker.split("&")[0]);
            avgLastPrice = httpLast.add(wssLast).divide(new BigDecimal(2), 4, RoundingMode.DOWN);
        }

        if (null == strHttpsTicker && null != strWssTicker) {
            avgLastPrice = new BigDecimal(strWssTicker.split("&")[0]);
        } else if (null != strWssTicker && null != strHttpsTicker) {
//            Object price = httpsTicker.get("price");
            httpLast = new BigDecimal(strHttpsTicker.split("&")[0]);
            avgLastPrice = httpLast;
        }
        logger.info("binance的交易对:{},httpLast的最新成交价为：{},wssLast的最新成交价为：{}", hashKey, httpLast, wssLast);
        logger.info("binance的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }


    public BigDecimal mergeByBitfinex(BBSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
        String httpsKey = bitfinexHttpsRedisKey + hashKey;
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
//        BitfinexResponseEntity httpsTicker = JSON.parseObject(strHttpsTicker, BitfinexResponseEntity.class);
        if (null == strHttpsTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else {
            avgLastPrice = new BigDecimal(strHttpsTicker.split("&")[0]);
        }
        logger.info("bitfinex的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }

    public BigDecimal mergeByOk(BBSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + "-" + symbol.split("_")[1];
        String httpsKey = okHttpsRedisKey + hashKey;
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
//        OkResponseEntity httpsTicker = JSON.parseObject(strHttpsTicker, OkResponseEntity.class);
        if (null == strHttpsTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else {
            avgLastPrice = new BigDecimal(strHttpsTicker.split("&")[0]);
        }
        logger.info("ok的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }

}
