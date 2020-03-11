//package com.hp.sh.expv3.bb.kline.service.impl;
//
//import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
//import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
//import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
//import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
//import com.hp.sh.expv3.bb.kline.service.BbKlineThirdDataService;
//import com.hp.sh.expv3.bb.kline.util.BBKlineUtil;
//import com.hp.sh.expv3.bb.kline.util.BbKlineRedisKeyUtil;
//import com.hp.sh.expv3.bb.kline.util.StringReplaceUtil;
//import com.hp.sh.expv3.config.redis.RedisUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.ZSetOperations;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.util.CollectionUtils;
//import redis.clients.jedis.Tuple;
//
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author BaiLiJun  on 2020/3/11
// */
//@Service
//@Transactional(rollbackFor = Exception.class)
//public class BbKlineThirdDataServiceImpl2 implements BbKlineThirdDataService{
//    @Value("${bb.kline.bbGroupIds}")
//    private Set<Integer> supportBbGroupIds;
//
//    @Value("${bb.kline.triggerBatchSize}")
//    private Integer triggerBatchSize;
//
//    @Value("${bb.kline.supportFrequenceString}")
//    private String supportFrequenceString;
//
//    @Autowired
//    @Qualifier("metadataRedisUtil")
//    private RedisUtil metadataRedisUtil;
//
//    @Autowired
//    @Qualifier("bbKlineOngoingRedisUtil")
//    private RedisUtil bbKlineOngoingRedisUtil;
//
//    @Value("${kline.bb.thirdDataUpdateEventPattern}")
//    private String thirdDataUpdateEventPattern;
//
//    @Value("${kline.bb.thirdDataPattern}")
//    private String thirdDataPattern;
//
//    @Value("${bb.kline}")
//    private String bbKlinePattern;
//
//    @Value("${bb.kline.updateEventPattern}")
//    private String updateEventPattern;
//
//    @Value("${bb.kline.thirdUpdate.enable}")
//    private int thirdUpdateEnable;
//
//    @Value("${bb.kline.thirdBatchSize}")
//    private int thirdBatchSize;
//
//    //    @Scheduled(cron = "*/1 * * * * *")
//    @Override
//    public void updateKlineByThirdData(){
//        List<BBSymbol> bbSymbols = listSymbol();
//
//        for (BBSymbol bbSymbol : bbSymbols) {
//            String asset = bbSymbol.getAsset();
//            String symbol = bbSymbol.getSymbol();
//            final int freq = 1;
//            String thirdDataUpdateEventKey = buildThirdDataUpdateEventKey(asset,symbol, freq);
//
//            String thirdDataPatternKey = buildThirdDataPatternKey(asset,symbol, freq);
//
//                final Set<Tuple> triggers = bbKlineOngoingRedisUtil.zpopmin(thirdDataUpdateEventKey, triggerBatchSize);
//                if (CollectionUtils.isEmpty(triggers)) {
//                    continue;
//                }
//
//                for (Tuple trigger : triggers) {
//                    final long ms = Double.valueOf(trigger.getScore()).longValue();
//                    // 时间大于等于当前时间的 忽略
//                    long currentMs = Calendar.getInstance().getTimeInMillis();
//                    if (ms >= currentMs) {
//                        continue;
//                    }
//                    long startMinutes = TimeUnit.MILLISECONDS.toMinutes(ms);
//                    long endMinutes = TimeUnit.MINUTES.toMillis(startMinutes + 1) - 1;
//
//                    // 若数据已存在，忽略
//                    final List<BBKLine> bbkLines = listKlineResource(asset, symbol, freq, startMinutes, endMinutes);
//                    if (null == bbkLines || bbkLines.isEmpty()) {
//                        continue;
//                    }else {
//                        BBKLine newKline = merge(asset, symbol, freq, startMinutes, bbkLines);
//                        saveOrUpdateKline(asset, symbol, freq, newKline);
//                        notifyKlineUpdate(asset, symbol, freq, startMinutes);
////                        BBKLine kline = buildKline(trades, asset, symbol, minute);
////
////                        // kline:from_exp:repair:BB:${asset}:${symbol}:${interval}:${minute}
////                        saveKline(kline, asset, symbol, 1, minute);
////
////                        // kline:from_exp:update:BB:${asset}:${symbol}:${interval}:${minute}
////                        notifyUpdate(asset, symbol, 1, minute);
//                    }
//
//                }
////                for (ZSetOperations.TypedTuple<String> tuple : task) {
////                    long minute = getMinute(tuple);
////
////
////
////                    // 若修复数据已存在，忽略 从redis kline:from_exp:repair:BB:${asset}:${symbol}:${minute}中取
////                    String repairkey = BbKLineKey.KLINE_BB_REPAIR_FROM_EXP + asset + ":" + symbol + ":" + 1;
////                    Set<ZSetOperations.TypedTuple<String>> repaired = templateDB0.opsForZSet().rangeWithScores(repairkey, 0, -1);
////
////                    if (null != repaired || !repaired.isEmpty()) {
////                        continue;
////                    }
////
////                    long startTimeInMs = TimeUnit.MINUTES.toMillis(minute);
////                    long endTimeInMs = TimeUnit.MINUTES.toMillis(minute + 1) - 1;
////                    List<BbTradeVo> trades = listTrade(asset, symbol, startTimeInMs, endTimeInMs);
////                    if (null == trades || trades.isEmpty()) {
////                        continue;
////                    } else {
////                        BBKLine kline = buildKline(trades, asset, symbol, minute);
////
////                        // kline:from_exp:repair:BB:${asset}:${symbol}:${interval}:${minute}
////                        saveKline(kline, asset, symbol, 1, minute);
////
////                        // kline:from_exp:update:BB:${asset}:${symbol}:${interval}:${minute}
////                        notifyUpdate( asset, symbol,1,minute);
////                    }
////
////                }
//
//
//
//        }
//
//    }
//
//
//    private void saveOrUpdateKline(String asset, String symbol, Integer targetFreq, BBKLine newKline) {
//        final String targetFreqRedisKey = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, targetFreq);
//        //删除老数据
//        bbKlineOngoingRedisUtil.zremrangeByScore(targetFreqRedisKey, newKline.getMs(), newKline.getMs());
//        //新增新数据
//        bbKlineOngoingRedisUtil.zadd(targetFreqRedisKey, new HashMap<String, Double>() {{
//            final String data = BBKlineUtil.kline2ArrayData(newKline);
//            put(data, Long.valueOf(newKline.getMs()).doubleValue());
//        }});
//
//    }
//
//    private List<BBKLine> listKlineResource(String asset, String symbol, Integer triggerFreq, Long startMinute, Long endMinute) {
//        final String triggerFreqRedisKey = BbKlineRedisKeyUtil.buildKlineDataRedisKey(bbKlinePattern, asset, symbol, triggerFreq);
//
//        final long startMs = TimeUnit.MINUTES.toMillis(startMinute);
//        final long endMs = TimeUnit.MINUTES.toMillis(endMinute);
//        final Set<String> klines = bbKlineOngoingRedisUtil.zrangeByScore(triggerFreqRedisKey, startMs + "", endMs + "", 0, Long.valueOf(endMinute - startMinute + 1).intValue());
//        List<BBKLine> list = new ArrayList<>();
//        // 按照时间minute升序
//        if (!klines.isEmpty()) {
//            for (String kline : klines) {
//                BBKLine bbkLine1 = BBKlineUtil.convert2KlineData(kline, triggerFreq);
//                list.add(bbkLine1);
//            }
//        }
//        List<BBKLine> sortedList = list.stream().sorted(Comparator.comparing(BBKLine::getMinute)).collect(Collectors.toList());
//
//        return sortedList;
//    }
//
//    private BBKLine merge(String asset, String symbol, Integer targetFreq, Long startMinute, List<BBKLine> bbkLines) {
//        final BBKLine bbkLine = new BBKLine();
//        bbkLine.setAsset(asset);
//        bbkLine.setSymbol(symbol);
//
//        BigDecimal highPrice = BigDecimal.ZERO;
//        BigDecimal lowPrice = new BigDecimal(String.valueOf(Long.MAX_VALUE));
//        BigDecimal openPrice = null;
//        BigDecimal closePrice = null;
//        BigDecimal volume = BigDecimal.ZERO;
//
//        for (BBKLine kLine : bbkLines) {
//            BigDecimal high = kLine.getHigh();
//            BigDecimal low = kLine.getHigh();
//            BigDecimal open = kLine.getHigh();
//            highPrice = (high.compareTo(highPrice) >= 0) ? high : highPrice;
//            lowPrice = (low.compareTo(lowPrice) <= 0) ? low : lowPrice;
//            openPrice = (null == openPrice) ? open : openPrice;
//            volume = volume.add(kLine.getVolume());
//        }
//
//        bbkLine.setHigh(highPrice);
//        bbkLine.setLow(lowPrice);
//        bbkLine.setOpen(openPrice);
//        bbkLine.setClose(closePrice);
//        bbkLine.setVolume(volume);
//        bbkLine.setFrequence(targetFreq);
//        bbkLine.setMinute(startMinute);
//        bbkLine.setMs(TimeUnit.MINUTES.toMillis(startMinute));
//        return bbkLine;
//    }
//
//    private List<BBSymbol> listSymbol() {
//        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
//        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
//        return list;
//    }
//
//    private BBKLine buildKline(List<BbTradeVo> trades, String asset, String symbol, long minute) {
//        BBKLine bBKLine = new BBKLine();
//        bBKLine.setAsset(asset);
//        bBKLine.setSymbol(symbol);
//        bBKLine.setFrequence(1);
//        bBKLine.setMinute(minute);
//
//        BigDecimal highPrice = BigDecimal.ZERO;
//        BigDecimal lowPrice = BigDecimal.ZERO;
//        BigDecimal openPrice = null;
//        BigDecimal closePrice = null;
//        BigDecimal volume = BigDecimal.ZERO;
//
//        for (BbTradeVo trade : trades) {
//            BigDecimal currentPrice = trade.getPrice();
//            highPrice = highPrice.compareTo(trade.getPrice()) >= 0 ? highPrice : currentPrice;
//            lowPrice = lowPrice.compareTo(trade.getPrice()) <= 0 ? lowPrice : currentPrice;
//            openPrice = null == openPrice ? currentPrice : openPrice;
//            closePrice = currentPrice;
//            volume = volume.add(trade.getNumber());
//        }
//
//        bBKLine.setHigh(highPrice);
//        bBKLine.setLow(lowPrice);
//        bBKLine.setOpen(openPrice);
//        bBKLine.setClose(closePrice);
//        bBKLine.setVolume(volume);
//
//        return bBKLine;
//    }
//
//    /**
//     * 返回第三方数据的key
//     * @param asset
//     * @param symbol
//     * @param freq
//     * @return 返回第三方数据的key
//     */
//    private String buildThirdDataUpdateEventKey(String asset, String symbol, int freq) {
//        String thirdDataUpdateEventKey = StringReplaceUtil.replace(thirdDataUpdateEventPattern, new HashMap<String, String>() {
//            {
//                put("asset", asset);
//                put("symbol", symbol);
//                put("freq", freq+"");
//            }
//        });
//        return thirdDataUpdateEventKey;
//    }
//
//
//    private String buildThirdDataPatternKey(String asset, String symbol, int freq) {
//        String thirdDataUpdateEventKey = StringReplaceUtil.replace(thirdDataPattern, new HashMap<String, String>() {
//            {
//                put("asset", asset);
//                put("symbol", symbol);
//                put("freq", freq+"");
//            }
//        });
//        return thirdDataUpdateEventKey;
//    }
//
//
//
//}
