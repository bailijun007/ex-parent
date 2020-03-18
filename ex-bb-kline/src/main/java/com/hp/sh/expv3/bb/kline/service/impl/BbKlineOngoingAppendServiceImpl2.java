package com.hp.sh.expv3.bb.kline.service.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBKlineTrade;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.BbKlineOngoingAppendService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.util.BbKlineRedisKeyUtil;
import com.hp.sh.expv3.bb.kline.util.StringReplaceUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPubSub;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/10
 */
@Service
public class BbKlineOngoingAppendServiceImpl2 implements BbKlineOngoingAppendService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${bb.trade.pattern}")
    private String bbTradePattern;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineOngoingRedisUtil;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Value("${bb.kline.updateEventPattern}")
    private String updateEventPattern;

    @Value("${bb.kline}")
    private String bbKlinePattern;

    @Value("${bb.kline.ongoingCalc.enable}")
    private int ongoingCalcEnable;

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            Runtime.getRuntime().availableProcessors() + 1,
            2L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10000000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.DiscardOldestPolicy()
    );

    //    @PostConstruct
    public void bbKlineBuild() {
        if (1 != ongoingCalcEnable) {
            return;
        }
        threadPool.execute(() -> trigger());

    }

    @Override
    public void trigger() {

        List<BBSymbol> bbSymbols = BBKlineUtil.listSymbol(metadataRedisUtil);

        for (BBSymbol bbSymbol : bbSymbols) {
            String asset = bbSymbol.getAsset();
            String symbol = bbSymbol.getSymbol();
            //监听 Exp 平台实时推送消息
            String channel = StringReplaceUtil.replace(bbTradePattern, new HashMap<String, String>() {
                {
                    put("asset", asset);
                    put("symbol", symbol);
                }
            });

            final ArrayBlockingQueue<List<BbTradeVo>> queue = new ArrayBlockingQueue<>(10000000);

            Executors.newSingleThreadExecutor().submit(
                    () ->
                            bbKlineOngoingRedisUtil.getOriginalJedis().subscribe(new JedisPubSub() {
                                @Override
                                public void onMessage(String channel, String msg) {
                                    logger.info("收到k线推送消息:{},{}", channel, msg);
                                    List<BbTradeVo> list = parseTrades(msg);
                                    queue.add(list);
                                }
                            }, channel)
            );

            Executors.newSingleThreadExecutor().submit(
                    () -> {
                        while (true) {
                            List<BbTradeVo> tradeList = new ArrayList<>();
                            for (int i = 0; i < 10; i++) {
                                final List<BbTradeVo> list = queue.poll();
                                if (null == list) {
                                    break;
                                } else {
                                    logger.info("{},{},{}", asset, symbol, list.size());
                                    tradeList.addAll(list);
                                }
                            }

                            if (tradeList.isEmpty()) {
                                Thread.sleep(1L);
                                continue;
                            }

                            logger.info("{},{},trade size:{}", asset, symbol, tradeList.size());

                            // 拆成不同的分钟
                            Map<Long, List<BbTradeVo>> minute2TradeList = tradeList.stream()
                                    .collect(Collectors.groupingBy(klineTrade -> TimeUnit.MILLISECONDS.toMinutes(klineTrade.getTradeTime())));

                            //1分钟kline 数据
                            for (Long minute : minute2TradeList.keySet()) {
                                List<BbTradeVo> trades = minute2TradeList.get(minute);
                                final int oneMinuteInterval = 1;
                                BBKLine newkLine = buildKline(trades, asset, symbol, oneMinuteInterval, minute);

                                BBKLine oldkLine = getOldKLine(asset, symbol, minute, oneMinuteInterval);

                                BBKLine mergedKline = append(oldkLine, newkLine);
                                saveKline(mergedKline, asset, symbol, minute, oneMinuteInterval);

                                notifyUpdate(asset, symbol, minute, oneMinuteInterval);
                            }
                        }
                    }
            );


        }
    }


    public BBKLine append(BBKLine oldkLine, BBKLine newkLine) {
        // oldKline 有可能是空，直接返回newKline
        if (null == oldkLine) {
            return newkLine;
        }
        newkLine.setHigh(newkLine.getHigh().max(oldkLine.getHigh()));
        newkLine.setLow(newkLine.getLow().min(oldkLine.getLow()));
        newkLine.setOpen(oldkLine.getOpen());
        newkLine.setVolume(newkLine.getVolume().add(oldkLine.getVolume()));
// close 不用修改
        return newkLine;
    }


    /*
     * kline:from_exp:repair:BB:${asset}:${symbol}:${minute}
     *   frequency 频率；1分钟
     */
    public void saveKline(BBKLine kline, String asset, String symbol, long minute, int frequency) {
        //向集合中插入元素，并设置分数
        String key = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, frequency);
        final long ms = TimeUnit.MINUTES.toMillis(minute);
        bbKlineOngoingRedisUtil.zremrangeByScore(key, ms, ms);
        final String data = BBKlineUtil.kline2ArrayData(kline);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
            put(data, Long.valueOf(ms).doubleValue());
        }});
    }

    public void notifyUpdate(String asset, String symbol, long minute, int frequency) {
        //向集合中插入元素，并设置分数
        String key = BbKlineRedisKeyUtil.buildKlineUpdateEventRedisKey(updateEventPattern, asset, symbol, frequency);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
                    put(BbKlineRedisKeyUtil.buildUpdateRedisMember(asset, symbol, frequency, minute), Long.valueOf(minute).doubleValue());
                }}
        );
    }

    public BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, int frequency, long minute) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(frequency);
        bBKLine.setMinute(minute);
        bBKLine.setMs(TimeUnit.MINUTES.toMillis(minute));

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

        return bBKLine;
    }

    private List<BbTradeVo> parseTrades(String msg) {
        BBKlineTrade bbKlineTrade = JSON.parseObject(msg, BBKlineTrade.class);
        return bbKlineTrade.getTrades();
    }

    public BBKLine getOldKLine(String asset, String symbol, long minute, int freq) {
        BBKLine bbkLine1 = null;
        long ms = TimeUnit.MINUTES.toMillis(minute);
        String key = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, freq);
        Set<String> range = bbKlineOngoingRedisUtil.zrangeByScore(key, "" + ms, ms + "", 0, 1);

        if (!range.isEmpty()) {
            final String s = new ArrayList<>(range).get(0);
//            JSON字符串转JSON对象
            bbkLine1 = BBKlineUtil.convert2KlineData(s, freq);
        }
        return bbkLine1;
    }

}
