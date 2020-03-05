package com.hp.sh.expv3.bb.extension.pubsub;

import com.alibaba.fastjson.JSON;
import com.gitee.hupadev.commons.cache.RedisPool;
import com.hp.sh.expv3.bb.extension.constant.BbKLineKey;
import com.hp.sh.expv3.bb.extension.pojo.BBKLine;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun on 2020/3/4
 */


public class BBKlineBuild {

    private static final Logger logger = LoggerFactory.getLogger(BBKlineBuild.class);

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Autowired
    private RedisPool redisPool;

    public void trigger() {

        final List<BBSymbol> bbSymbols = listSymbol();
        for (BBSymbol bbSymbol : bbSymbols) {
            String channel = "bb:trade:${asset}:${symbol}";
            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            templateDB0.getConnectionFactory().getConnection().subscribe(new MessageListener() {
                @Override
                public void onMessage(Message message, byte[] pattern) {
                    final String msg = new String(message.getBody());

                    final List<BbTradeVo> bbTradeVos = listTrade(msg);

                    Map<Long, List<BbTradeVo>> minute2TradeList = null; // 拆成不同的分钟

                    for (Map.Entry<Long, List<BbTradeVo>> longListEntry : minute2TradeList.entrySet()) {
                        long minute = longListEntry.getKey();
                        final List<BbTradeVo> trades = longListEntry.getValue();
                        final BBKLine newkLine = buildKline(trades, asset, symbol, minute);

                        final BBKLine oldkLine = getKLine(asset, symbol, minute);

                        final BBKLine mergedKline = merge(newkLine, oldkLine);

                        saveKline(mergedKline, asset, symbol, 1,minute);

                        notifyUpdate(minute, asset, symbol);

                    }

                }
            }, channel.getBytes());

        }


    }

    private BBKLine merge(BBKLine newkLine, BBKLine oldkLine) {
        // oldKline 有可能是空，直接返回newKline
        return null;
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
    private void saveKline(BBKLine kline, String asset, String symbol, int interval,long minute) {
        //向集合中插入元素，并设置分数
        templateDB0.opsForZSet().add(BbKLineKey.BB_KLINE_REPAIR + asset + ":" + symbol+":1", JSON.toJSONString(kline), minute);
    }

    private void notifyUpdate(long minute, String asset, String symbol) {
        //向集合中插入元素，并设置分数
        templateDB0.opsForZSet().add(BbKLineKey.BB_KLINE_UPDATE + asset + ":" + symbol + ":" + minute, asset + "#" + symbol + "#" + minute, minute);

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
        List<BbTradeVo> voList = null;// bbTradeExtService.queryByTimeInterval(asset, symbol, startTimeInMs, endTimeInMs);
        //返回 对象集合以时间升序 再以id升序
        List<BbTradeVo> sortedList = voList.stream().sorted(Comparator.comparing(BbTradeVo::getTradeTime).thenComparing(BbTradeVo::getId)).collect(Collectors.toList());
        return sortedList;
    }


    BBKLine getKLine(String asset, String symbol, long minute) {
        return null;
    }


}
