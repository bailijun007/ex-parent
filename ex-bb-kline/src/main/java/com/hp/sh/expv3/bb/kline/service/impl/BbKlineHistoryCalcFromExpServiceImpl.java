package com.hp.sh.expv3.bb.kline.service.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.BbKlineHistoryCalcFromExpService;
import com.hp.sh.expv3.bb.kline.service.BbTradeExtService;
import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
import com.hp.sh.expv3.bb.kline.util.StringReplaceUtil;
import com.hp.sh.expv3.config.redis.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/11
 */
@Service
public class BbKlineHistoryCalcFromExpServiceImpl implements BbKlineHistoryCalcFromExpService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${bb.kline.bbGroupIds}")
    private Set<Integer> supportBbGroupIds;

    @Value("${bb.kline.supportFrequenceString}")
    private String supportFrequenceString;

    @Value("${bb.kline.expHistoryBatchSize}")
    private Integer expHistoryBatchSize;

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Autowired
    @Qualifier("bbKlineOngoingRedisUtil")
    private RedisUtil bbKlineExpHistoryRedisUtil;


    @Value("${bb.kline.notifyUpdate}")
    private String bbKlineFromExpUpdate;

    @Value("${bb.kline.repair}")
    private String bbKlineFromExpRepair;

    @Value("${bb.kline.task}")
    private String bbKlineFromExpTask;

    @Value("${bb.kline.expHistory.enable}")
    private int bbKlineExpHistoryEnable;

    @Autowired
    private BbTradeExtService bbTradeExtService;

    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
            2,
            Runtime.getRuntime().availableProcessors() + 1,
            2L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(100000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );

    @Scheduled(cron = "*/1 * * * * *")
    public void execute(){
        if (1 != bbKlineExpHistoryEnable) {
            return;
        }else {
            threadPool.execute(()->repairKlineFromExp());
        }
    }


    public void repairKlineFromExp() {

        List<BBSymbol> bbSymbols = listSymbol();
        List<BBSymbol> targetBbSymbols = filterBbSymbols(bbSymbols);

        for (BBSymbol bbSymbol : targetBbSymbols) {

            final String asset = bbSymbol.getAsset();
            final String symbol = bbSymbol.getSymbol();
            int freq = 1;
            {
                String taskKey = buildBbKlineFromExpTaskRedisKey(asset, symbol, freq);
                String repairkey = buildBbKlineFromExpRepairKey(asset, symbol, freq);
                String notifyUpdateKey = buildBbKlineFromExpUpdateKey(asset, symbol, freq);

                final Set<Tuple> task = bbKlineExpHistoryRedisUtil.zpopmin(taskKey, expHistoryBatchSize);
                if (CollectionUtils.isEmpty(task)) {
                    continue;
                }
                for (Tuple tuple : task) {
                    Double score = tuple.getScore();
                    long ms = Double.valueOf(score).longValue();
                    long maxMs = TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(ms) + 1) - 1;

                    // 时间大于等于当前时间的 忽略
                    long currentMs = Calendar.getInstance().getTimeInMillis();
                    if (ms >= currentMs) {
                        continue;
                    }

                    // 若修复数据已存在，忽略 从redis kline:from_exp:repair:BB:${asset}:${symbol}:${minute}中取
                    final Set<String> set = bbKlineExpHistoryRedisUtil.zrangeByScore(repairkey, ms + "", maxMs + "", 0, Long.valueOf(maxMs - ms).intValue() + 1);

                    if (!set.isEmpty()) {
                        continue;
                    }
//                    if (null != set || !set.isEmpty()) {
//                        continue;
//                    }

                    List<BbTradeVo> trades = listTrade(asset, symbol, ms, maxMs);
                    if (null == trades || trades.isEmpty()) {
                        continue;
                    } else {
                        BBKLine kline = buildKline(trades, asset, symbol, ms, freq);

                        logger.info("build kline data:{}",kline.toString());

                        saveKline(repairkey, kline);

                        notifyUpdate(notifyUpdateKey,ms);
                    }

                }

            }

        }
    }

    private String buildBbKlineFromExpUpdateKey(String asset, String symbol, int freq) {
        String bbKlineFromExpUpdateKey = StringReplaceUtil.replace(bbKlineFromExpUpdate, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", freq + "");
            }
        });
        return bbKlineFromExpUpdateKey;
    }

    private void notifyUpdate(String notifyUpdateKey,long ms) {
        HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
        scoreMembers.put(ms+ "", Long.valueOf(ms).doubleValue());
        bbKlineExpHistoryRedisUtil.zadd(notifyUpdateKey, scoreMembers);
    }

    private void saveKline(String repairkey, BBKLine kline) {
        HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
        final String data = BBKlineUtil.kline2ArrayData(kline);
        scoreMembers.put(data, Long.valueOf(kline.getMs()).doubleValue());
        bbKlineExpHistoryRedisUtil.zadd(repairkey, scoreMembers);
    }

    private List<BbTradeVo> listTrade(String asset, String symbol, long ms, long maxMs) {
        List<BbTradeVo> voList = bbTradeExtService.queryByTimeInterval(asset, symbol, ms, maxMs);
        //返回 对象集合以时间升序 再以id升序
        List<BbTradeVo> sortedList = voList.stream().sorted(Comparator.comparing(BbTradeVo::getTradeTime).thenComparing(BbTradeVo::getId)).collect(Collectors.toList());
        return sortedList;
    }

    private String buildBbKlineFromExpRepairKey(String asset, String symbol, int freq) {
        String BbKlineFromExpTaskRedisKey = StringReplaceUtil.replace(bbKlineFromExpRepair, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", freq + "");
            }
        });
        return BbKlineFromExpTaskRedisKey;
    }


    private String buildBbKlineFromExpTaskRedisKey(String asset, String symbol, int freq) {
        String BbKlineFromExpTaskRedisKey = StringReplaceUtil.replace(bbKlineFromExpTask, new HashMap<String, String>() {
            {
                put("asset", asset);
                put("symbol", symbol);
                put("freq", freq + "");
            }
        });
        return BbKlineFromExpTaskRedisKey;
    }

    private List<BBSymbol> listSymbol() {
        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
        return list;
    }

    private List<BBSymbol> filterBbSymbols(List<BBSymbol> bbSymbols) {
        return bbSymbols.stream()
                .filter(symbol -> supportBbGroupIds.contains(symbol.getBbGroupId()))
                .collect(Collectors.toList());
    }

    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, long ms, int freq) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(freq);
        bBKLine.setMs(ms);
        bBKLine.setMinute(TimeUnit.MILLISECONDS.toMinutes(ms));

        BigDecimal highPrice = BigDecimal.ZERO;
        BigDecimal lowPrice = new BigDecimal(String.valueOf(Long.MAX_VALUE));
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

}
