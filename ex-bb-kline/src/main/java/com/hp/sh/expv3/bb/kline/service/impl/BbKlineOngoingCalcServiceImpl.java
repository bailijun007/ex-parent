package com.hp.sh.expv3.bb.kline.service.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.constant.BbextendConst;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBKlineTrade;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.BbKlineOngoingCalcService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.util.StringReplaceUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/10
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbKlineOngoingCalcServiceImpl implements BbKlineOngoingCalcService {
    private static final Logger logger = LoggerFactory.getLogger(BbKlineOngoingCalcServiceImpl.class);

    @Value("${bb.trade.pattern}")
    private String bbTradePattern;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineOngoingRedisUtil;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Value("${bb.kline.updateEventPattern}")
    private String bbKlineUpdateEventPattern;

    @Value("${bb.kline}")
    private String bbKlinePattern;


    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            Runtime.getRuntime().availableProcessors() + 1,
            2L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Value("${bb.kline.ongoingCalc.enable:1}")
    private int ongoingCalcEnable;


    @PostConstruct
    public void bbKlineBuild() {
        if (ongoingCalcEnable == 1) {
            threadPool.execute(() -> trigger());
        }
    }

    @Override
    public void trigger() {
        List<BBSymbol> bbSymbols = listSymbol();

        for (BBSymbol bbSymbol : bbSymbols) {
            String asset = bbSymbol.getAsset();
            String symbol = bbSymbol.getSymbol();
            String channel = StringReplaceUtil.replace(bbTradePattern, new HashMap<String, String>() {
                {
                    put("asset", asset);
                    put("symbol", symbol);
                }
            });

            bbKlineOngoingRedisUtil.getOriginalJedis().subscribe(new JedisPubSub() {
                @Override
                public void onMessage(String channel, String msg) {

                    logger.info("收到k线推送消息:{}" + msg);
                    List<BbTradeVo> list = extractTrade(msg);
                    // 拆成不同的分钟
                    Map<Long, List<BbTradeVo>> minute2TradeList = list.stream()
                            .collect(Collectors.groupingBy(klineTrade -> TimeUnit.MILLISECONDS.toMinutes(klineTrade.getTradeTime())));

                    final int oneMinuteFreq = 1;
                    //1分钟kline 数据
                    for (Long oneMinute : minute2TradeList.keySet()) {
                        List<BbTradeVo> trades = minute2TradeList.get(oneMinute);

                        final Long ms = BBKlineUtil.minutesToMillis(oneMinute);

                        BBKLine newkLine = buildKline(trades, asset, symbol, oneMinuteFreq, ms);

                        BBKLine oldkLine = getOldKLine(asset, symbol, ms, oneMinuteFreq);

                        BBKLine mergedKline = merge(oldkLine, newkLine);
                        saveKline(mergedKline, asset, symbol, ms, oneMinuteFreq);

                        notifyUpdate(asset, symbol, ms, oneMinuteFreq);
                    }
                }
            }, channel);
        }
    }


    public BBKLine merge(BBKLine oldkLine, BBKLine newkLine) {
        // oldKline 有可能是空，直接返回newKline
        if (null == oldkLine) {
            return newkLine;
        }
        newkLine.setHigh(newkLine.getHigh().max(oldkLine.getHigh()));
        newkLine.setLow(newkLine.getLow().min(oldkLine.getLow()));
        newkLine.setOpen(oldkLine.getOpen());
        newkLine.setVolume(newkLine.getVolume().add(oldkLine.getVolume()));

        logger.info("合并k线数据:{}" + newkLine);

        return newkLine;
    }

    private List<BBSymbol> listSymbol() {
        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
        return list;
    }

    /*
     * bb:kline:%{asset}:%{symbol}:%{freq}
     *   frequency 频率；1分钟
     */
    public void saveKline(BBKLine kline, String asset, String symbol, long ms, int frequency) {
        String key = buildKlineSaveRedisKey(asset, symbol, frequency);

        bbKlineOngoingRedisUtil.zremrangeByScore(key, ms, ms);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
            final String klineData = BBKlineUtil.buildKlineData(kline);
            put(klineData, Long.valueOf(ms).doubleValue());
        }});
        logger.info("保存的K线数据为:{}" + kline);
    }


    public void notifyUpdate(String asset, String symbol, long ms, int frequency) {
        //向集合中插入元素，并设置分数
        final String key1 = buildUpdateRedisMember(asset, symbol, frequency, ms);
        String key = buildUpdateRedisKey(asset, symbol, frequency);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
                    put(key1, Long.valueOf(ms).doubleValue());
                }}
        );
        logger.info("通知:{}" + key1);
    }

    public BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, int frequency, long ms) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(frequency);
        bBKLine.setMs(ms);

        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice = new BigDecimal(String.valueOf(Long.MAX_VALUE));
        BigDecimal openPrice = null;
        BigDecimal closePrice = null;
        BigDecimal volume = BigDecimal.ZERO;

        for (BbTradeVo trade : trades) {
            BigDecimal currentPrice = trade.getPrice();
            highPrice = (highPrice.compareTo(currentPrice) >= 0) ? highPrice : currentPrice;
            lowPrice = (lowPrice.compareTo(currentPrice) <= 0) ? lowPrice : currentPrice;
            openPrice = (null == openPrice) ? currentPrice : openPrice;
            closePrice = currentPrice;
            volume = volume.add(trade.getNumber());
        }

        bBKLine.setHigh(highPrice);
        bBKLine.setLow(lowPrice);
        bBKLine.setOpen(openPrice);
        bBKLine.setClose(closePrice);
        bBKLine.setVolume(volume);

        logger.info("生成实时k线数据:{}" + bBKLine);

        return bBKLine;
    }

    private List<BbTradeVo> extractTrade(String msg) {
        BBKlineTrade bbKlineTrade = JSON.parseObject(msg, BBKlineTrade.class);
        return bbKlineTrade.getTrades();
    }

    public BBKLine getOldKLine(String asset, String symbol, long ms, int frequency) {
        //
        BBKLine bbkLine1 = null;
        String key = buildKlineSaveRedisKey(asset, symbol, frequency);

        Set<String> range = bbKlineOngoingRedisUtil.zrangeByScore(key, "" + ms, ms + "", 0, 1);

        if (!range.isEmpty()) {
            final String s = new ArrayList<>(range).get(0);
            bbkLine1 = BBKlineUtil.convertKlineData(s);
        }
        logger.info("旧k线数据:{}" + bbkLine1);
        return bbkLine1;
    }



    private String buildUpdateRedisKey(String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(bbKlineUpdateEventPattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }

    private String buildUpdateRedisMember(String asset, String symbol, int frequency, long minute) {
        return StringReplaceUtil.replace(BbextendConst.BB_KLINE_UPDATE_MEMBER, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
            put("minute", "" + minute);
        }});
    }

    private String buildKlineSaveRedisKey(String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(bbKlinePattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }
}
