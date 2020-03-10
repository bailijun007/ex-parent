//package com.hp.sh.expv3.bb.extension.job;
//
//import com.alibaba.fastjson.JSON;
//import com.hp.sh.expv3.bb.extension.constant.BbKLineKey;
//import com.hp.sh.expv3.bb.extension.pojo.BBKLine;
//import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
//import com.hp.sh.expv3.bb.extension.pubsub.BBKlineBuild;
//import com.hp.sh.expv3.bb.extension.util.StringReplaceUtil;
//import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.redis.core.*;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import java.util.*;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//
///**
// * 5分钟k线数据
// *
// * @author BaiLiJun  on 2020/3/9
// */
//@Component
//public class FiveMinuteklineData {
//
//    @Resource(name = "templateDB0")
//    private StringRedisTemplate templateDB0;
//
//    @Resource(name = "templateDB5")
//    private StringRedisTemplate templateDB5;
//
//    @Value("${bb.kline.update}")
//    private String bbKlineUpdatePattern;
//
//    @Value("${bb.kline}")
//    private String bbKlinePattern;
//
//    @Value("${bb.kline}")
//    private BBKlineBuild bbKlineBuild;
//
////    @Scheduled(cron = "0 0/1 * * * ?")
//    public void getFiveMinuteklineData() {
//        List<BBSymbol> bbSymbols = listSymbol();
//        List<BbTradeVo> list = new ArrayList<>();
//
//        for (BBSymbol bbSymbol : bbSymbols) {
//            String asset = bbSymbol.getAsset();
//            String symbol = bbSymbol.getSymbol();
//            final int oneMinuteInterval = 1;
//            final int fiveMinuteInterval = 5;
//            Long minScore =0L;
//            while (true) {
//                String key = buildKlineSaveRedisKey(asset, symbol, oneMinuteInterval);
//                final Set<ZSetOperations.TypedTuple<String>> tuples = templateDB5.opsForZSet().rangeWithScores(key, 0, 1);
//                for (ZSetOperations.TypedTuple<String> tuple : tuples) {
//                    minScore = Double.valueOf(tuple.getScore()).longValue();
//                }
//                 long maxScore = TimeUnit.MINUTES.toMillis(minScore+5);
//                final Set<String> set = templateDB5.opsForZSet().rangeByScore(key, minScore, maxScore);
//
//                for (String s : set) {
//                    final BbTradeVo bbTradeVo = JSON.parseObject(s, BbTradeVo.class);
//                    list.add(bbTradeVo);
//                }
//
//                // 拆成不同的分钟
//                Map<Long, List<BbTradeVo>> minute2TradeList = list.stream()
//                        .collect(Collectors.groupingBy(klineTrade -> klineTrade.getTradeTime()));
//
//                //5分钟kline 数据
//                for (Long ms : minute2TradeList.keySet()) {
//                    long oneMinute = TimeUnit.MILLISECONDS.toMinutes(ms);
//                    List<BbTradeVo> trades = minute2TradeList.get(ms);
////                    final int oneMinuteInterval = 1;
//                    BBKLine newkLine = bbKlineBuild.buildKline(trades, asset, symbol, fiveMinuteInterval, oneMinute);
//
//                    BBKLine oldkLine = bbKlineBuild.getOldKLine(asset, symbol, oneMinute, fiveMinuteInterval);
//
//                    BBKLine mergedKline = bbKlineBuild.merge(oldkLine, newkLine);
//                    bbKlineBuild.saveKline(mergedKline, asset, symbol, oneMinute, fiveMinuteInterval);
//
//                    bbKlineBuild.notifyUpdate(asset, symbol, oneMinute, fiveMinuteInterval);
//                }
//
//            }
//
//
//        }
//
//    }
//
//    private List<BBSymbol> listSymbol() {
//        HashOperations opsForHash = templateDB0.opsForHash();
//        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(BbKLineKey.BB_SYMBOL, ScanOptions.NONE);
//
//        List<BBSymbol> list = new ArrayList<>();
//        while (curosr.hasNext()) {
//            Map.Entry<String, Object> entry = curosr.next();
//            Object o = entry.getValue();
//            BBSymbol bBSymbolVO = JSON.parseObject(o.toString(), BBSymbol.class);
//            list.add(bBSymbolVO);
//        }
//        return list;
//    }
//
//
//    private String buildUpdateRedisKey(String asset, String symbol, int frequency) {
//        return StringReplaceUtil.replace(bbKlineUpdatePattern, new HashMap<String, String>() {
//            {
//                put("asset", asset);
//                put("symbol", symbol);
//                put("freq", "" + frequency);
//            }
//        });
//
////        return BbKLineKey.KLINE_BB_UPDATE + asset + ":" + symbol + ":" + interval;
//    }
//
//    private String buildKlineSaveRedisKey(String asset, String symbol, int frequency) {
//        return StringReplaceUtil.replace(bbKlinePattern, new HashMap<String, String>() {{
//            put("asset", asset);
//            put("symbol", symbol);
//            put("freq", "" + frequency);
//        }});
//    }
//
//}
