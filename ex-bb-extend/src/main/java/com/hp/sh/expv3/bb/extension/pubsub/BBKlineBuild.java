package com.hp.sh.expv3.bb.extension.pubsub;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.constant.BbKLineKey;
import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.pojo.BBKLine;
import com.hp.sh.expv3.bb.extension.pojo.BBKlineTrade;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.util.StringReplaceUtil;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun on 2020/3/4
 */

@Component
public class BBKlineBuild {

    private static final Logger logger = LoggerFactory.getLogger(BBKlineBuild.class);

//    @Resource(name = "templateDB0")
//    private StringRedisTemplate templateDB0;

//    @Resource(name = "klineTemplateDB5")
//    private StringRedisTemplate klineTemplateDB5;

    @Value("${bb.trade.pattern}")
    private String bbTradePattern;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineOngoingRedisUtil;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Value("${bb.kline.update}")
    private String bbKlineUpdatePattern;

    @Value("${bb.kline}")
    private String bbKlinePattern;

    private static   ScheduledExecutorService timer = Executors.newScheduledThreadPool(2);

    @PostConstruct
    public void bbKlineBuild() {
//        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
//                2,
//                Runtime.getRuntime().availableProcessors()+1,
//                2L,TimeUnit.SECONDS,
//                new LinkedBlockingQueue<Runnable>(100000),
//                Executors.defaultThreadFactory(),
//                new  ThreadPoolExecutor.AbortPolicy()
//        );
//
//        threadPool.execute(()->trigger());


        timer.scheduleAtFixedRate(() -> {
            trigger();
        }, 0, 10, TimeUnit.SECONDS);
    }


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
                    List<BbTradeVo> bbTradeVos = listTrade(msg);

                    // 拆成不同的分钟
                    Map<Long, List<BbTradeVo>> minute2TradeList = bbTradeVos.stream()
                            .collect(Collectors.groupingBy(klineTrade -> klineTrade.getTradeTime()));

                    //1分钟kline 数据
                    for (Long ms : minute2TradeList.keySet()) {
                        long oneMinute = TimeUnit.MILLISECONDS.toMinutes(ms);
                        List<BbTradeVo> trades = minute2TradeList.get(ms);
                        final int oneMinuteInterval = 1;
                        BBKLine newkLine = buildKline(trades, asset, symbol, oneMinuteInterval, oneMinute);

                        BBKLine oldkLine = getOldKLine(asset, symbol, oneMinute, oneMinuteInterval);

                        BBKLine mergedKline = merge(oldkLine, newkLine);
                        saveKline(mergedKline, asset, symbol, oneMinute, oneMinuteInterval);

                        notifyUpdate(asset, symbol, oneMinute, oneMinuteInterval);
                    }
                }
            }, channel);
        }
    }


    private BBKLine merge(BBKLine oldkLine, BBKLine newkLine) {
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

    private List<BBSymbol> listSymbol() {
        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
        return list;
    }

    /*
     * kline:from_exp:repair:BB:${asset}:${symbol}:${minute}
     *   interval 频率；1分钟
     */
    private void saveKline(BBKLine kline, String asset, String symbol, long minute, int interval) {
        //向集合中插入元素，并设置分数
        String key = buildKlineSaveRedisKey(asset, symbol, interval);


        bbKlineOngoingRedisUtil.zremrangeByScore(key, minute, minute);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
            put(JSON.toJSONString(kline), Long.valueOf(minute).doubleValue());
        }});
    }

    private void notifyUpdate(String asset, String symbol, long minute, int frequency) {
        //向集合中插入元素，并设置分数
        String key = buildUpdateRedisKey(asset, symbol, frequency);
        bbKlineOngoingRedisUtil.zadd(key, new HashMap<String, Double>() {{
                    put(buildUpdateRedisMember(asset, symbol, frequency), Long.valueOf(minute).doubleValue());
                }}
        );
    }

    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, int interval, long minute) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(interval);
        bBKLine.setMinute(minute);

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

    private List<BbTradeVo> listTrade(String msg) {
        BBKlineTrade bbKlineTrade = JSON.parseObject(msg, BBKlineTrade.class);
        return bbKlineTrade.getTrades();
    }

    BBKLine getOldKLine(String asset, String symbol, long minute, int interval) {
        BBKLine bbkLine1 = null;
        String key = buildKlineSaveRedisKey(asset, symbol, interval);
//        Set<String> range = klineTemplateDB5.opsForZSet().rangeByScore(key, minute, minute);

        Set<String> range = bbKlineOngoingRedisUtil.zrangeByScore(key, "" + minute, minute + "", 0, 1);

        if (!range.isEmpty()) {
            final String s = new ArrayList<>(range).get(0);
//            JSON字符串转JSON对象
            bbkLine1 = JSON.parseObject(s, BBKLine.class);
        }
        return bbkLine1;
    }


    private String buildUpdateRedisKey(String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(bbKlineUpdatePattern, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
        }});
    }

    private String buildUpdateRedisMember(String asset, String symbol, int frequency) {
        return StringReplaceUtil.replace(BbextendConst.BB_KLINE_UPDATE_MEMBER, new HashMap<String, String>() {{
            put("asset", asset);
            put("symbol", symbol);
            put("freq", "" + frequency);
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
