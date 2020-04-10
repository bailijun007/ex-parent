package com.hp.sh.expv3.bb.grab3rdData.job;

import com.alibaba.fastjson.JSON;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    @Value("${bb.trade.symbols}")
    private String symbols;

    @Value("${bb.trade.bbGroupIds}")
    private Integer bbGroupId;

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


    //    @Scheduled(cron = "*/1 * * * * *")
    public void merge() {
        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        if (!CollectionUtils.isEmpty(bbSymbolList)) {
            for (BBSymbol bbSymbol : bbSymbolList) {
                mergeByZb(bbSymbol);
                mergeByBinance(bbSymbol);
                mergeByBitfinex(bbSymbol);
                mergeByOk(bbSymbol);
            }
        }
    }


    //    @Scheduled(cron = "*/1 * * * * *")
    public BigDecimal mergeByZb(BBSymbol bbSymbol) {
//        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        BigDecimal avgLastPrice = BigDecimal.ZERO;
//        if (!CollectionUtils.isEmpty(bbSymbolList)) {
//            for (BBSymbol bbSymbol : bbSymbolList) {
                String symbol = bbSymbol.getSymbol().toLowerCase();
                String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
                String httpsKey = zbHttpsRedisKey + hashKey;
                String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
                ZbTickerData httpsTicker = JSON.parseObject(strHttpsTicker, ZbTickerData.class);

                String wssKey = zbWssRedisKey + hashKey;
                String strWssTicker = metadataDb5RedisUtil.get(wssKey);
                ZbTickerData wssTicker = JSON.parseObject(strWssTicker, ZbTickerData.class);
//                if (null == httpsTicker && null == wssTicker) {
//                    continue;
//                }
                BigDecimal httpLast = BigDecimal.ZERO;
                BigDecimal wssLast = BigDecimal.ZERO;
                if (null != httpsTicker) {
                    httpLast = httpsTicker.getLast();
                } else if (null != wssTicker) {
                    wssLast = wssTicker.getLast();
                }

                avgLastPrice = httpLast.add(wssLast).divide(new BigDecimal(2), 4, RoundingMode.DOWN);
                logger.info("zb的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);

//                this.saveMerge(bbSymbol,avgLastPrice);
//            }
//        }
        return avgLastPrice;
    }


    private void saveMerge(BBSymbol bbSymbol, BigDecimal avgLastPrice) {
        String key = "ticker:bb:lastPrice:" + bbSymbol.getAsset() + ":" + bbSymbol.getSymbol();
        HashMap<String, BigDecimal> map = new HashMap<>();
        map.put(bbSymbol.getSymbol(), avgLastPrice);
        metadataDb5RedisUtil.hmset(key, map);
    }

    //    @Scheduled(cron = "*/1 * * * * *")
    public BigDecimal mergeByBinance(BBSymbol bbSymbol) {
//        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
        BigDecimal avgLastPrice = BigDecimal.ZERO;
//        if (!CollectionUtils.isEmpty(bbSymbolList)) {
//            for (BBSymbol bbSymbol : bbSymbolList) {
                String symbol = bbSymbol.getSymbol();
                String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
                String httpsKey = binanceHttpsRedisKey + hashKey;
                String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
                BinanceResponseData httpsTicker = JSON.parseObject(strHttpsTicker, BinanceResponseData.class);

                String wssKey = binanceWssRedisKey + hashKey;
                String strWssTicker = metadataDb5RedisUtil.get(wssKey);
                BinanceResponseData wssTicker = JSON.parseObject(strWssTicker, BinanceResponseData.class);
//                if (null == httpsTicker && null == wssTicker) {
//                    continue;
//                }
                BigDecimal httpLast = BigDecimal.ZERO;
                BigDecimal wssLast = BigDecimal.ZERO;
                if (null != httpsTicker) {
                    httpLast = httpsTicker.getC();
                } else if (null != wssTicker) {
                    wssLast = wssTicker.getC();
                }

                avgLastPrice = httpLast.add(wssLast).divide(new BigDecimal(2), 4, RoundingMode.DOWN);
                logger.info("binance的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);

//                this.saveMerge(bbSymbol,avgLastPrice);
//            }
//        }
        return avgLastPrice;
    }


    public BigDecimal mergeByBitfinex(BBSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
//        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
//        if (!CollectionUtils.isEmpty(bbSymbolList)) {
//            for (BBSymbol bbSymbol : bbSymbolList) {
                String symbol = bbSymbol.getSymbol();
                String hashKey = symbol.split("_")[0] + symbol.split("_")[1];
                String httpsKey = bitfinexHttpsRedisKey + hashKey;
                String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
                BitfinexResponseEntity httpsTicker = JSON.parseObject(strHttpsTicker, BitfinexResponseEntity.class);
                if (null == httpsTicker) {
                    avgLastPrice=BigDecimal.ZERO;
                }
                avgLastPrice = httpsTicker.getLastPrice();
                logger.info("bitfinex的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
//                this.saveMerge(bbSymbol,avgLastPrice);
//            }
//        }

        return avgLastPrice;
    }

    public BigDecimal mergeByOk(BBSymbol bbSymbol) {
        BigDecimal avgLastPrice = BigDecimal.ZERO;
//        List<BBSymbol> bbSymbolList = supportBbGroupIdsJobService.getSymbols();
//        if (!CollectionUtils.isEmpty(bbSymbolList)) {
//            for (BBSymbol bbSymbol : bbSymbolList) {
                String symbol = bbSymbol.getSymbol();
                String hashKey = symbol.split("_")[0] + "-" + symbol.split("_")[1];
                String httpsKey = okHttpsRedisKey + hashKey;
                String strHttpsTicker = metadataDb5RedisUtil.get(httpsKey);
                OkResponseEntity httpsTicker = JSON.parseObject(strHttpsTicker, OkResponseEntity.class);
                if (null == httpsTicker) {
                    avgLastPrice=BigDecimal.ZERO;
                }
                avgLastPrice = httpsTicker.getLast();
                logger.info("ok的交易对:{},merge后的最新成交价为：{}", hashKey, avgLastPrice);
//                this.saveMerge(bbSymbol,avgLastPrice);
//            }
//        }

        return avgLastPrice;
    }

}
