package com.hp.sh.expv3.pc.grab3rdData.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.grab3rdData.pojo.*;
import com.hp.sh.expv3.pc.grab3rdData.service.SupportBbGroupIdsJobService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
            for (PcSymbol pcSymbol : bbSymbolList) {
                BigDecimal binanceAvgPrice = mergeByBinance(pcSymbol);
                logger.info("binance最新成交均价为:{},", binanceAvgPrice);
                BigDecimal okAvgPrice = mergeByOk(pcSymbol);
                logger.info("ok最新成交均价为:{},", okAvgPrice);
                BigDecimal avgLastPrice = binanceAvgPrice.add(okAvgPrice).divide(new BigDecimal(2), 4, RoundingMode.DOWN);
                logger.info("asset={},symbol={},最终最新成交均价为:{},", pcSymbol.getAsset(),pcSymbol.getSymbol(),avgLastPrice);
                  if(avgLastPrice.compareTo(BigDecimal.ZERO)!=0){
                      saveMerge(pcSymbol, avgLastPrice);
                  }
            }
        }
    }


    public BigDecimal mergeByZb(PcSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol().toLowerCase();
        String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
        String httpsKey = zbHttpsRedisKey + hashKey;
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
        ZbTickerData httpsTicker = JSON.parseObject(strHttpsTicker, ZbTickerData.class);

        String wssKey = zbWssRedisKey + hashKey;
        String strWssTicker = metadataDb5RedisUtil.get(wssKey);
        ZbTickerData wssTicker = JSON.parseObject(strWssTicker, ZbTickerData.class);
        BigDecimal httpLast = BigDecimal.ZERO;
        BigDecimal wssLast = BigDecimal.ZERO;
        if (null == httpsTicker && null == wssTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else if (null != httpsTicker && null != wssTicker) {
            httpLast = httpsTicker.getLast();
            wssLast = wssTicker.getLast();
            avgLastPrice = httpLast.add(wssLast).divide(new BigDecimal(2), 4, RoundingMode.DOWN);
        }

        if (null == httpsTicker) {
            avgLastPrice = wssTicker.getLast();
        } else if (null == wssTicker) {
            avgLastPrice = httpsTicker.getLast();
        }

        logger.info("zb的交易对:{},httpLast的最新成交价为：{},wssLast的最新成交价为：{}", hashKey, httpLast, wssLast);
        logger.info("zb的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);

        return avgLastPrice;
    }


    private void saveMerge(PcSymbol bbSymbol, BigDecimal avgLastPrice) {
        String key = "ticker:pc:lastPrice:" + bbSymbol.getAsset() + ":" + bbSymbol.getSymbol();
        HashMap<String, BigDecimal> map = new HashMap<>();
        map.put(bbSymbol.getSymbol(), avgLastPrice);
        metadataDb5RedisUtil.hmset(key, map);
    }

    public BigDecimal mergeByBinance(PcSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
        String wssKey = binanceWssRedisKey + hashKey;
        String strWssTicker = metadataDb5RedisUtil.get(wssKey);
        BinanceResponseData wssTicker = JSON.parseObject(strWssTicker, BinanceResponseData.class);
        BigDecimal wssLast = BigDecimal.ZERO;
        if ( null == wssTicker) {
            avgLastPrice = BigDecimal.ZERO;
        } else  {
            avgLastPrice = wssTicker.getP();
        }

        logger.info("binance的交易对:{},wssLast的最新成交价为：{}", hashKey, wssLast);
        logger.info("binance的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }


    public BigDecimal mergeByBitfinex(PcSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
        String httpsKey = bitfinexHttpsRedisKey + hashKey;
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
        BitfinexResponseEntity httpsTicker = JSON.parseObject(strHttpsTicker, BitfinexResponseEntity.class);
        if (null == httpsTicker) {
            avgLastPrice = BigDecimal.ZERO;
        }
        avgLastPrice = httpsTicker.getLastPrice() == null ? BigDecimal.ZERO : httpsTicker.getLastPrice();
        logger.info("bitfinex的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }

    public BigDecimal mergeByOk(PcSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
        String symbol = bbSymbol.getSymbol();
        String hashKey = symbol.split("_")[0] + "-" + symbol.split("_")[1];
        String httpsKey = okHttpsRedisKey + hashKey;
        String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
        OkResponseEntity httpsTicker = JSON.parseObject(strHttpsTicker, OkResponseEntity.class);
        if (null == httpsTicker) {
            avgLastPrice = BigDecimal.ZERO;
        }else {
            avgLastPrice = httpsTicker.getLast();
        }
        logger.info("ok的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
        return avgLastPrice;
    }

}
