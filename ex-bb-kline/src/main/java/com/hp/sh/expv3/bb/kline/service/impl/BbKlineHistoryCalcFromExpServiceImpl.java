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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Tuple;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/11
 */
@Service
public class BbKlineHistoryCalcFromExpServiceImpl implements BbKlineHistoryCalcFromExpService {
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

    public void repairKlineFromExp() {

        if (1 != bbKlineExpHistoryEnable) {
            return;
        }

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

                    if (null != set || !set.isEmpty()) {
                        continue;
                    }

                    List<BbTradeVo> trades = listTrade(asset, symbol, ms, maxMs);
                    if (null == trades || trades.isEmpty()) {
                        continue;
                    } else {
                        BBKLine kline = buildKline(trades, asset, symbol, ms, freq);

                        // kline:from_exp:repair:BB:${asset}:${symbol}:${interval}:${minute}
                        saveKline(repairkey, kline);

                        // kline:from_exp:update:BB:${asset}:${symbol}:${interval}:${minute}
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

    private List<BBKLine> listBbKline(String asset, String symbol, Long minMs, Long maxMs, int freq) {
//        String thirdDataKey = buildThirdDataKey(asset, symbol, freq);
//        final Set<String> klines = bbKlineOngoingRedisUtil.zrangeByScore(thirdDataKey, "" + minMs, "" + maxMs, 0, Long.valueOf(maxMs - minMs).intValue() + 1);
//        List<BBKLine> list = new ArrayList<>();
//        // 按照时间minute升序
//        if (!klines.isEmpty()) {
//            for (String kline : klines) {
//                BBKLine bbkLine1 = BBKlineUtil.convert2KlineData(kline, freq);
//                list.add(bbkLine1);
//            }
//        }
//        return list;\
        List<BBKLine> list = new ArrayList<>();

        Long minute = TimeUnit.MILLISECONDS.toMinutes(minMs);

        List<BbTradeVo> voList = bbTradeExtService.queryByTimeInterval(asset, symbol, minMs, maxMs);
        //返回 对象集合以时间升序 再以id升序
        List<BbTradeVo> sortedList = voList.stream().sorted(Comparator.comparing(BbTradeVo::getTradeTime).thenComparing(BbTradeVo::getId)).collect(Collectors.toList());
        BBKLine bbkLine = buildKline(sortedList, asset, symbol, minute, freq);

        return list;
    }

    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, long minute, int freq) {
        BBKLine bBKLine = new BBKLine();
        bBKLine.setAsset(asset);
        bBKLine.setSymbol(symbol);
        bBKLine.setFrequence(freq);
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

}
