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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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


    public void trigger() {
        List<BBSymbol> bbSymbols = listSymbol();
        for (BBSymbol bbSymbol : bbSymbols) {
            String asset = bbSymbol.getAsset();
            String symbol = bbSymbol.getSymbol();
            String channel = BbKLineKey.BB_TRADE + ":" + asset + ":" + symbol;

            templateDB0.getConnectionFactory().getConnection().subscribe(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    String msg = new String(message.getBody());

                    List<BbTradeVo> bbTradeVos = listTrade(msg);

                    // 拆成不同的分钟
                    Map<Long, List<BbTradeVo>> minute2TradeList = bbTradeVos.stream()
                            .collect(Collectors.groupingBy(klineTrade -> klineTrade.getTradeTime()));

                    for (Long ms : minute2TradeList.keySet()) {
                        long minute = TimeUnit.MILLISECONDS.toMinutes(ms);
                        List<BbTradeVo> trades = minute2TradeList.get(ms);
                        BBKLine newkLine = buildKline(trades, asset, symbol, minute);

                        BBKLine oldkLine = getOldKLine(asset, symbol, minute);

                        BBKLine mergedKline = merge(newkLine, oldkLine);

                        saveKline(mergedKline, asset, symbol, minute);

                        notifyUpdate(minute, asset, symbol);
                    }

                }
            }, channel.getBytes());

        }


    }

    private BBKLine merge(BBKLine newkLine, BBKLine oldkLine) {
        // oldKline 有可能是空，直接返回newKline
        if (null == oldkLine) {
            return newkLine;
        }
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(newkLine.getAsset().equals(oldkLine.getAsset()) ? newkLine.getAsset() : null);
        bBKLine.setSymbol(newkLine.getSymbol().equals(oldkLine.getSymbol()) ? newkLine.getAsset() : null);
        bBKLine.setSequence(1);
        bBKLine.setMinute(newkLine.getMinute() == oldkLine.getMinute() ? newkLine.getMinute() : null);

        bBKLine.setHigh(newkLine.getHigh().compareTo(oldkLine.getHigh()) >= 0 ? newkLine.getHigh() : oldkLine.getHigh());
        bBKLine.setLow(newkLine.getLow().compareTo(oldkLine.getLow()) <= 0 ? oldkLine.getLow() : newkLine.getHigh());
        bBKLine.setOpen(oldkLine.getOpen());
        bBKLine.setClose(newkLine.getClose());
        bBKLine.setVolume(newkLine.getVolume().add(oldkLine.getVolume()));

        return bBKLine;
    }


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
    private void saveKline(BBKLine kline, String asset, String symbol, long minute) {
        //向集合中插入元素，并设置分数
        klineTemplateDB5.opsForZSet().add(BbKLineKey.BB_KLINE_REPAIR + asset + ":" + symbol + ":" + minute, JSON.toJSONString(kline), minute);
    }

    private void notifyUpdate(long minute, String asset, String symbol) {
        //向集合中插入元素，并设置分数
        klineTemplateDB5.opsForZSet().add(BbKLineKey.BB_KLINE_UPDATE + asset + ":" + symbol + ":" + minute, asset + "#" + symbol + "#" + minute, minute);
    }

    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, long minute) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setSequence(1);
        bBKLine.setMinute(minute);

        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice = BigDecimal.ZERO;
        BigDecimal openPrice = null;
        BigDecimal closePrice = BigDecimal.ZERO;
        BigDecimal volume = BigDecimal.ZERO;

        for (BbTradeVo trade : trades) {
            BigDecimal currentPrice = trade.getPrice();
            highPrice = highPrice.compareTo(trade.getPrice()) >= 0 ? highPrice : currentPrice;
            lowPrice = lowPrice.compareTo(trade.getPrice()) <= 0 ? lowPrice : currentPrice;
            openPrice = null == openPrice ? currentPrice : openPrice;
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
        return bbKlineTrade.getkLineTrades();
    }


    BBKLine getOldKLine(String asset, String symbol, long minute) {
        BBKLine bbkLine1 = null;
        Set<String> range = klineTemplateDB5.opsForZSet()
                .rangeByScore(BbKLineKey.BB_KLINE_REPAIR + asset + ":" + symbol + ":" + minute, minute, minute);

        if (!range.isEmpty()) {
            final String s = new ArrayList<>(range).get(0);
            System.out.println("s = " + s);
//            JSON字符串转JSON对象
            bbkLine1 = JSON.parseObject(s, BBKLine.class);
        }
        return bbkLine1;
    }


}
