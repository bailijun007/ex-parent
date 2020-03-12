package com.hp.sh.expv3.bb.extension.job;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.constant.BbKLineKey;
import com.hp.sh.expv3.bb.extension.pojo.BBKLine;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.service.BbTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/4
 */
@Component
public class BbKLineJob {
    private static final Logger logger = LoggerFactory.getLogger(BbKLineJob.class);

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;

    @Autowired
    private BbTradeExtService bbTradeExtService;

    /**
     * fixedDelay = 5,fixedRate = 5
     */
//    @Scheduled(cron = "*/5 * * * * *")
    public void kLineJobTask() {
        List<BBSymbol> bbSymbols = listSymbol();

        for (BBSymbol bbSymbol : bbSymbols) {
            String asset = bbSymbol.getAsset();
            String symbol = bbSymbol.getSymbol();
            while (true) {
                //返回集合内元素的排名，以及分数（从小到大）
                String taskKey = BbKLineKey.KLINE_BB_TASK_FROM_EXP + asset + ":" + symbol;
                Set<ZSetOperations.TypedTuple<String>> task = templateDB0.opsForZSet().rangeWithScores(taskKey, 0, -1);
                if (CollectionUtils.isEmpty(task)) {
                    continue;
                }
                for (ZSetOperations.TypedTuple<String> tuple : task) {
                    long minute = getMinute(tuple);

                    // 时间大于等于当前时间的 忽略
                    long currentMinute = TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().getTimeInMillis());
                    if (minute >= currentMinute) {
                        continue;
                    }

                    // 若修复数据已存在，忽略 从redis kline:from_exp:repair:BB:${asset}:${symbol}:${minute}中取
                    String repairkey = BbKLineKey.KLINE_BB_REPAIR_FROM_EXP + asset + ":" + symbol + ":" + 1;
                    Set<ZSetOperations.TypedTuple<String>> repaired = templateDB0.opsForZSet().rangeWithScores(repairkey, 0, -1);

                    if (null != repaired || !repaired.isEmpty()) {
                        continue;
                    }

                    long startTimeInMs = TimeUnit.MINUTES.toMillis(minute);
                    long endTimeInMs = TimeUnit.MINUTES.toMillis(minute + 1) - 1;
                    List<BbTradeVo> trades = listTrade(asset, symbol, startTimeInMs, endTimeInMs);
                    if (null == trades || trades.isEmpty()) {
                        continue;
                    } else {
                        BBKLine kline = buildKline(trades, asset, symbol, minute);

                        // kline:from_exp:repair:BB:${asset}:${symbol}:${interval}:${minute}
                        saveKline(kline, asset, symbol, 1, minute);

                        // kline:from_exp:update:BB:${asset}:${symbol}:${interval}:${minute}
                        notifyUpdate( asset, symbol,1,minute);
                    }

                }
            }


        }

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

    // kline:from_exp:repair:BB:${asset}:${symbol}:${interval}:${minute}
    private void saveKline(BBKLine kline, String asset, String symbol,int interval,long minute) {
        //向集合中插入元素，并设置分数
        templateDB0.opsForZSet().add(BbKLineKey.KLINE_BB_REPAIR_FROM_EXP + asset + ":" + symbol+":"+interval+":"+minute, JSON.toJSONString(kline), minute);
    }

    // kline:from_exp:update:BB:${asset}:${symbol}:${interval}:${minute}
    private void notifyUpdate(String asset, String symbol,int interval,long minute) {
        //向集合中插入元素，并设置分数
        templateDB0.opsForZSet().add(BbKLineKey.BB_KLINE_UPDATE + asset + ":" + symbol + ":" +interval+":"+ minute, asset + "#" + symbol + "#" + minute, minute);

    }

    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, long minute) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(1);
        bBKLine.setMinute(minute);

        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice = BigDecimal.ZERO;
        BigDecimal openPrice = null;
        BigDecimal closePrice = null;
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

    /*
     *sql 中 按照ID排序，分批查询，查到没有返回结果时截止
     * 查询全部数据之后，再按照时间升序，id 升序 在内存中排序
     */
    private List<BbTradeVo> listTrade(String asset, String symbol, long startTimeInMs, long endTimeInMs) {
        List<BbTradeVo> voList = bbTradeExtService.queryByTimeInterval(asset, symbol, startTimeInMs, endTimeInMs);
        //返回 对象集合以时间升序 再以id升序
        List<BbTradeVo> sortedList = voList.stream().sorted(Comparator.comparing(BbTradeVo::getTradeTime).thenComparing(BbTradeVo::getId)).collect(Collectors.toList());
        return sortedList;
    }


    long getMinute(ZSetOperations.TypedTuple<String> tuple) {
//        System.out.println(tuple.getValue() + " : " + tuple.getScore());
        logger.info("k线1分钟时间为: {}", tuple.getValue());
        //            JSON字符串转JSON对象
        final BBKLine bbkLine = JSON.parseObject(tuple.getValue(), BBKLine.class);
        return bbkLine.getMinute();
    }

    Set<ZSetOperations.TypedTuple<String>> getRepairedData(long minute) {
        return new HashSet<>();
    }


}
