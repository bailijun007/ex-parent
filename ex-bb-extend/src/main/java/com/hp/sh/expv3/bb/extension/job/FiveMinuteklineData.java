//package com.hp.sh.expv3.bb.extension.job;
//
//import com.alibaba.fastjson.JSON;
//import com.hp.sh.expv3.bb.extension.constant.BbKLineKey;
//import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
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
//    @Resource(name = "klineTemplateDB5")
//    private StringRedisTemplate klineTemplateDB5;
//
//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void getFiveMinuteklineData() {
//        List<BBSymbol> bbSymbols = listSymbol();
//        List<BbTradeVo> list = new CopyOnWriteArrayList<>();
//
//        for (BBSymbol bbSymbol : bbSymbols) {
//            String asset = bbSymbol.getAsset();
//            String symbol = bbSymbol.getSymbol();
//            final int oneMinuteInterval = 1;
//            final int fiveMinuteInterval = 5;
//            while (true) {
//                String key = buildUpdateRedisKey(asset, symbol, oneMinuteInterval);
//                klineTemplateDB5.opsForZSet().range(key, 0, 1);
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
//    @Value("${bb.kline.update}")
//    private String bbKlineUpdatePattern;
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
//}
