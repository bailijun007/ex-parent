package com.hp.sh.expv3.bb.extension.job;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
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
import java.util.stream.Stream;

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
    @Scheduled(cron = "*/5 * * * * *")
    public void kLineJobTask() {
        List<BBSymbol> bbSymbols = listSymbol();

        for (BBSymbol bbSymbol : bbSymbols) {
            String asset = bbSymbol.getAsset();
            String symbol = bbSymbol.getSymbol();
            while (true) {
                //返回集合内元素的排名，以及分数（从小到大）
                Set<ZSetOperations.TypedTuple<String>> task = templateDB0.opsForZSet().rangeWithScores("kline:from_exp:task:BB:" + asset + ":" + symbol, 0, -1);
                if (CollectionUtils.isEmpty(task)) {
                    break;
                }
                for (ZSetOperations.TypedTuple<String> tuple : task) {
                    long minute = getMinute(tuple);

                    // 时间大于等于当前时间的 忽略
                    long currentMinute = TimeUnit.MILLISECONDS.toMinutes(Calendar.getInstance().getTimeInMillis());
                    if (minute >= currentMinute) {
                        continue;
                    }

                    // 若修复数据已存在，忽略 从redis kline:from_exp:repair:BB:${asset}:${symbol}:${minute}中取
                    Set<ZSetOperations.TypedTuple<String>> repaired = templateDB0.opsForZSet().rangeWithScores("kline:from_exp:repair:BB:" + asset + ":" + symbol + ":" + minute, 0, -1);

//                    repaired = getRepairedData(minute);
                    if (null != repaired || !repaired.isEmpty()) {
                        continue;
                    }

                    long startTimeInMs = TimeUnit.MINUTES.toMillis(minute);
                    long endTimeInMs = TimeUnit.MINUTES.toMillis(minute + 1) - 1;
                    List<BbTradeVo> trades = listTrade(asset, symbol, startTimeInMs, endTimeInMs);
                    if (null == trades || trades.isEmpty()) {
                        continue;
                    } else {
                        BBKLine kline = buildKline(trades);

                        saveKline(kline); // kline:from_exp:repair:BB:${asset}:${symbol}

                        notifyUpdate(minute); // kline:from_exp:update:BB:${asset}:${symbol}
                    }

                }
            }


        }

    }

    private List<BBSymbol> listSymbol() {
        String key = "bb_symbol";
        HashOperations opsForHash = templateDB0.opsForHash();
        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(key, ScanOptions.NONE);

        List<BBSymbol> list = new ArrayList<>();
        while (curosr.hasNext()) {
            Map.Entry<String, Object> entry = curosr.next();
            Object o = entry.getValue();
            BBSymbol bBSymbolVO = JSON.parseObject(o.toString(), BBSymbol.class);
            list.add(bBSymbolVO);
        }
        return list;
    }

    private void saveKline(BBKLine kline) {
    }

    private void notifyUpdate(long minute) {

    }

    private BBKLine buildKline(List<BbTradeVo> trades) {
        BBKLine bBKLine = new BBKLine();
//        bBKLine.setAsset();
//        bBKLine.setSymbol();
//        bBKLine.setSequence();
//        bBKLine.setMinute();
//        bBKLine.setVolume();
//        bBKLine.setHigh();
//        bBKLine.setLow();
//        bBKLine.setOpen();
//        bBKLine.setClose();


        for (BbTradeVo trade : trades) {

        }

        return null;
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
        return 0L;
    }

    Set<ZSetOperations.TypedTuple<String>> getRepairedData(long minute) {
        return new HashSet<>();
    }



}
