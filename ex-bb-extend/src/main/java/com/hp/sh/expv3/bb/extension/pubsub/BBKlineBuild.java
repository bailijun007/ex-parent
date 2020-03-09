package com.hp.sh.expv3.bb.extension.pubsub;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.constant.BbKLineKey;
import com.hp.sh.expv3.bb.extension.pojo.BBKLine;
import com.hp.sh.expv3.bb.extension.pojo.BBKlineTrade;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun on 2020/3/4
 */

@Component
public class BBKlineBuild {

    private static final Logger logger = LoggerFactory.getLogger(BBKlineBuild.class);

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "klineTemplateDB5")
    private StringRedisTemplate klineTemplateDB5;


    @PostConstruct
    public void trigger() {
        List<BBSymbol> bbSymbols = listSymbol();
        List<BbTradeVo> list =new CopyOnWriteArrayList<>();

        for (BBSymbol bbSymbol : bbSymbols) {
            String asset = bbSymbol.getAsset();
            String symbol = bbSymbol.getSymbol();
            String channel = BbKLineKey.BB_TRADE + ":" + asset + ":" + symbol;

            templateDB0.getConnectionFactory().getConnection().subscribe(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    String msg = new String(message.getBody());
                    logger.info("收到k线推送消息:{}" + msg);
                    List<BbTradeVo> bbTradeVos = listTrade(msg);

                    list.addAll(bbTradeVos);

                    // 拆成不同的分钟
                    Map<Long, List<BbTradeVo>> minute2TradeList = list.stream()
                            .collect(Collectors.groupingBy(klineTrade -> klineTrade.getTradeTime()));

                    for (Long ms : minute2TradeList.keySet()) {
                        long minute = TimeUnit.MILLISECONDS.toMinutes(ms);
                        List<BbTradeVo> trades = minute2TradeList.get(ms);
                        BBKLine newkLine = buildKline(trades, asset, symbol, minute);

                        BBKLine oldkLine = getOldKLine(asset, symbol, minute, 1);

                        BBKLine mergedKline = merge(oldkLine, newkLine);
                        saveKline(mergedKline, asset, symbol, minute, 1);

                        notifyUpdate(asset, symbol, minute, 1);
                    }

                }
            }, channel.getBytes());

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

        return newkLine;
    }
//1,2,3,4,5
//        2->1 1,3,4,5
//        3->1 1,4,5
//        4->1 1,5
//        5->1 1

    private List<BBSymbol> listSymbol() {
        HashOperations opsForHash = templateDB0.opsForHash();
        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(BbKLineKey.BB_SYMBOL, ScanOptions.NONE);

        List<BBSymbol> list = new ArrayList<>();
        while (curosr.hasNext()) {
            Map.Entry<String, Object> entry = curosr.next();
            Object o = entry.getValue();
            BBSymbol bBSymbolVO = JSON.parseObject(o.toString(), BBSymbol.class);
            list.add(bBSymbolVO);
        }
        return list;
    }

    /*
     * kline:from_exp:repair:BB:${asset}:${symbol}:${minute}
     *   interval 频率；1分钟
     */
    private void saveKline(BBKLine kline, String asset, String symbol, long minute, int interval) {
        //向集合中插入元素，并设置分数
        String key = buildKlineSaveRedisKey(asset, symbol, interval);
        klineTemplateDB5.opsForZSet().removeRangeByScore(key, minute, minute);
        klineTemplateDB5.opsForZSet().add(key, JSON.toJSONString(kline), minute);
    }

    private String buildUpdateRedisKey(String asset, String symbol, int interval) {
        return BbKLineKey.KLINE_BB_UPDATE + asset + ":" + symbol + ":" + interval;
    }

    private String buildKlineSaveRedisKey(String asset, String symbol, int interval) {
        return BbKLineKey.KLINE_BB + asset + ":" + symbol + ":" + interval;
    }

    private void notifyUpdate(String asset, String symbol, long minute, int interval) {
        //向集合中插入元素，并设置分数
        String key = buildUpdateRedisKey(asset, symbol, interval);
        klineTemplateDB5.opsForZSet().add(key, asset + "#" + symbol + "#" + interval+"#"+minute, minute);
    }

    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, long minute) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(1);
        bBKLine.setMinute(minute);

        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice =new BigDecimal(String.valueOf(Long.MAX_VALUE));
        BigDecimal openPrice = null;
        BigDecimal closePrice = null;
        BigDecimal volume = BigDecimal.ZERO;

        for (BbTradeVo trade : trades) {
            BigDecimal currentPrice = trade.getPrice();
            highPrice = highPrice.compareTo(currentPrice) >= 0 ? highPrice : currentPrice;
            lowPrice=lowPrice.compareTo(currentPrice)<=0?lowPrice:currentPrice;
            openPrice = null == openPrice ? currentPrice : openPrice;
            closePrice = currentPrice;
            volume = volume.add(trade.getNumber());
        }

//         BigDecimal lowPrice = trades.stream().map(trade -> trade.getPrice()).min((t1, t2) -> t1.compareTo(t2)).get();

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
        Set<String> range = klineTemplateDB5.opsForZSet()
                .rangeByScore(key, minute, minute);

        if (!range.isEmpty()) {
            final String s = new ArrayList<>(range).get(0);
//            JSON字符串转JSON对象
            bbkLine1 = JSON.parseObject(s, BBKLine.class);
        }
        return bbkLine1;
    }


}
