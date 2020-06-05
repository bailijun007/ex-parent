//package com.hp.sh.expv3.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.hp.sh.expv3.service.impl.NewAssetDefaultPrice;
//import com.hp.sh.expv3.pojo.KlineDataPo;
//import com.hp.sh.expv3.service.INewAssetDefaultKlineDataService;
//import com.hp.sh.expv3.util.KlineUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//
//import javax.annotation.Resource;
//import java.math.BigDecimal;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * @author BaiLiJun  on 2020/6/4
// */
//@Service
//public class NewAssetDefaultKlineDataServiceImpl implements INewAssetDefaultKlineDataService {
//
//    private static final Logger logger = LoggerFactory.getLogger(NewAssetDefaultKlineDataServiceImpl.class);
//
//    @Resource(name = "metadataTemplateDB5")
//    private StringRedisTemplate templateDB5;
//
//    @Autowired
//    private NewAssetDefaultPrice newAssetDefaultPriceService;
//
//    @Override
//    public void getDefaultKlineData(BigDecimal price, Long timestamp, String asset, String symbol) {
//        long minusDay = 24 * 60 * 60 * 1000;
//        long minusMin = 60 * 1000;
//        Long start = timestamp - minusDay * 60;
//        Long startInMin = start;
//        List<BigDecimal> list = new ArrayList<>();
//        List<Long> dayTimeList = new ArrayList<>();
//        List<Long> minTimeList = new ArrayList<>();
//        for (int i = 0; i < 60; i++) {
//            start = start + minusDay;
//            dayTimeList.add(start);
//        }
//
////        BigDecimal minPrice = new BigDecimal("0.12");
//        for (int i = 0; i < 1440; i++) {
//            BigDecimal sub = getSub();
//            price = new BigDecimal("0.12");
//            if (i % 2 == 0) {
//                price = price.add(sub).add(new BigDecimal("0.001"));
//            } else {
//                price = price.subtract(sub);
//            }
//
//            if (price.compareTo(new BigDecimal("0.15")) >= 0) {
////                BigDecimal multiply = sub.multiply(new BigDecimal("100"));
//                price = price.subtract(sub).subtract(sub);
//            } else if (price.compareTo(new BigDecimal("0.12")) <= 0) {
//                price = price.add(sub).add(sub).add(sub);
//            }
//
//            if (i >= 1420) {
//                price = price.add(sub);
//            }
////            System.out.println("price = " + price);
//            list.add(price);
//        }
//
//
//        for (int i = 0; i < dayTimeList.size(); i++) {
//            for (int j = 0; j < 1440; j++) {
//                startInMin = startInMin + minusMin;
//                minTimeList.add(startInMin);
//            }
//        }
//
//        list.add(price);
//        minTimeList.add(timestamp);
//
//        List<KlineDataPo> poList = new ArrayList<>();
//        for (int i = 0; i < dayTimeList.size(); i++) {
//            BigDecimal sub = getSub();
//            if (i % 3 == 0&& i <= 50) {
//                sub = sub.add(new BigDecimal("0.015"));
//            } else if (i % 2 == 0 && i <= 50) {
//                sub = sub.add(new BigDecimal("0.01"));
//            } else {
//                sub = sub.add(new BigDecimal("0.005"));
//            }
//            if (sub.compareTo(new BigDecimal("0.15")) >= 0) {
//                sub = sub.subtract(new BigDecimal("0.008"));
//            } else if (sub.compareTo(new BigDecimal("0.12")) <= 0) {
//                sub = sub.add(new BigDecimal("0.005"));
//            }
//            if (i >= 50) {
//                sub = sub.add(new BigDecimal("0.015"));
//            }
//            for (int j = 0; j < 1440; j++) {
//                KlineDataPo klineDataPo = new KlineDataPo();
//                BigDecimal basePrice = list.get(j);
//                klineDataPo.setOpenTime(minTimeList.get(j));
//                klineDataPo.setOpen(basePrice.add(sub));
//                klineDataPo.setHigh(basePrice.add(sub).add(new BigDecimal("0.001")));
//                klineDataPo.setLow(basePrice.subtract(sub).subtract(new BigDecimal("0.001")));
//                klineDataPo.setClose(list.get(j + 1).add(sub));
//                BigDecimal volume = basePrice.multiply(new BigDecimal("" + (int) (Math.random() * 20)));
//                klineDataPo.setVolume(volume.add(BigDecimal.TEN));
//                poList.add(klineDataPo);
//            }
//        }
//
//        for (int i = 0; i < poList.size(); i++) {
//            poList.get(i).setOpenTime(minTimeList.get(i));
//            System.out.println("openPrice=" + poList.get(i).getOpen());
////            if (i % 1440 == 0) {
////                System.out.println("openPrice=" + poList.get(i).getOpen());
////            }
//        }
//
////        logger.info("生成的k线数据为：{}", JSON.toJSONString(map));
//        String dataRedisKey = "candle:bb:" + asset + ":" + symbol + ":" + 1;
//        String updateRedisKey = "bb:kline:updateEvent:" + asset + ":" + symbol + ":" + 1;
//
//       // saveAndNotify(dataRedisKey, updateRedisKey, timestamp - minusDay * 60, timestamp, poList);
//
//    }
//
//    private BigDecimal getSub() {
//        BigDecimal[] arr = {new BigDecimal("0.001"), new BigDecimal("0.0028"), new BigDecimal("0.0015"), new BigDecimal("0.002"), new BigDecimal("0.0023"), new BigDecimal("0.003"), new BigDecimal("0.005")};
//        int index = (int) (Math.random() * arr.length);
//        BigDecimal sub = arr[index];
//        return sub;
//    }
//
//    /**
//     * 保存并发出通知
//     *
//     * @param dataRedisKey   保存数据的redis key
//     * @param updateRedisKey 通知的redis key
//     * @param openTimeBegin  开始时间
//     * @param openTimeEnd    结束时间
//     * @param klineDataPos   数据
//     */
//    public void saveAndNotify(String dataRedisKey, String updateRedisKey, Long openTimeBegin, Long openTimeEnd, List<KlineDataPo> klineDataPos) {
//        if (CollectionUtils.isEmpty(klineDataPos)) {
//            return;
//        }
//        //删除旧数据
//        templateDB5.opsForZSet().removeRangeByScore(dataRedisKey, openTimeBegin.doubleValue(), openTimeEnd.doubleValue());
//
//        for (KlineDataPo klineDataPo : klineDataPos) {
//            String data = KlineUtil.kline2ArrayData(klineDataPo);
//            //保存新数据
//            double score = klineDataPo.getOpenTime().doubleValue();
//            templateDB5.opsForZSet().add(dataRedisKey, data, score);
//            //通知
//            templateDB5.opsForZSet().add(updateRedisKey, score + "", score);
//        }
//
//    }
//
//
//}
