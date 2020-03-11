//package com.hp.sh.expv3.bb.kline.service.impl;
//
//import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
//import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
//import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
//import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
//import com.hp.sh.expv3.bb.kline.service.BbKlineThirdDataService;
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
//import java.util.*;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * @author BaiLiJun  on 2020/3/11
// */
//@Service
//@Transactional(rollbackFor = Exception.class)
//public class BbKlineThirdDataServiceImpl implements BbKlineThirdDataService {
//    @Value("${bb.kline.triggerBatchSize}")
//    private Integer triggerBatchSize;
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
//
//    //    @Scheduled(cron = "*/1 * * * * *")
//    public void getThirdDataJobTask(){
//        List<BBSymbol> bbSymbols = listSymbol();
//
//        for (BBSymbol bbSymbol : bbSymbols) {
//            String asset = bbSymbol.getAsset();
//            String symbol = bbSymbol.getSymbol();
//            String thirdDataUpdateEventKey = StringReplaceUtil.replace(thirdDataUpdateEventPattern, new HashMap<String, String>() {
//                {
//                    put("asset", asset);
//                    put("symbol", symbol);
//                }
//            });
//
//            String thirdDataPatternKey = StringReplaceUtil.replace(thirdDataPattern, new HashMap<String, String>() {
//                {
//                    put("asset", asset);
//                    put("symbol", symbol);
//                    put("freq", "1");
//                }
//            });
//
//            while (true) {
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
//                    long startTimeInMs = ms;
//                    long endTimeInMs = TimeUnit.MINUTES.toMillis(TimeUnit.MILLISECONDS.toMinutes(ms) + 1) - 1;
//                    // 若修复数据已存在，忽略
//                    bbKlineOngoingRedisUtil.zrangeByScore();
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
//            }
//
//
//        }
//
//    }
//
//    private List<BBSymbol> listSymbol() {
//        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
//        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
//        return list;
//    }
//}
