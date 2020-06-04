package com.hp.sh.expv3.service.impl;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pojo.KlineDataPo;
import com.hp.sh.expv3.service.INewAssetDefaultKlineDataService;
import com.hp.sh.expv3.util.KlineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/6/4
 */
@Service
public class NewAssetDefaultKlineDataServiceImpl implements INewAssetDefaultKlineDataService {

    private static final Logger logger = LoggerFactory.getLogger(NewAssetDefaultKlineDataServiceImpl.class);

    @Resource(name = "metadataTemplateDB5")
    private StringRedisTemplate templateDB5;


    @Override
    public void getDefaultKlineData(BigDecimal price, Long timestamp, String asset, String symbol) {
        long minusDay = 24 * 60 * 60 * 1000;
        long minusMin = 60 * 1000;
        Long start = timestamp - minusDay * 60;
        Long startInMin = start;
        List<BigDecimal> list = new ArrayList<>();
        List<Long> dayTimeList = new ArrayList<>();
        List<Long> minTimeList = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            start = start + minusDay;
            dayTimeList.add(start);
        }

        for (int i = 0; i < 1440; i++) {
            BigDecimal sub = getSub();
            if (i % 2 == 0) {
                price = price.add(sub);
            } else {
                price = price.subtract(sub);
            }
            list.add(price);

        }

        for (int i = 0; i < dayTimeList.size(); i++) {
            for (int j = 0; j < 1440; j++) {
                startInMin = startInMin + minusMin;
                minTimeList.add(startInMin);
            }
        }


        list.add(price);
        minTimeList.add(timestamp);
//        List<BigDecimal> sortedlist = list.stream().sorted().collect(Collectors.toList());
        Map<Long, List<KlineDataPo>> map = new HashMap<>();
        for (int i = 0; i < dayTimeList.size(); i++) {
            List<KlineDataPo> poList = new ArrayList<>();
            for (int j = 0; j < 1440; j++) {
                BigDecimal sub = getSub();
                KlineDataPo klineDataPo = new KlineDataPo();
                BigDecimal basePrice = list.get(j);
                klineDataPo.setOpenTime(minTimeList.get(j));
                klineDataPo.setOpen(basePrice);
                klineDataPo.setHigh(basePrice.add(sub));
                klineDataPo.setLow(basePrice.subtract(sub).subtract(sub));
                klineDataPo.setClose(list.get(j + 1));
                BigDecimal volume = basePrice.multiply(new BigDecimal("" + (int) (Math.random() * 20)));
                klineDataPo.setVolume(volume.add(BigDecimal.TEN));
                poList.add(klineDataPo);
            }
            map.put(dayTimeList.get(i), poList);
        }

//        logger.info("生成的k线数据为：{}", JSON.toJSONString(map));
        String dataRedisKey = "candle:bb:" + asset + ":" + symbol + ":" + 1;
        String updateRedisKey = "bb:kline:updateEvent:" + asset + ":" + symbol + ":" + 1;

        saveAndNotify(dataRedisKey, updateRedisKey, timestamp - minusDay * 60, timestamp, map);

    }

    private BigDecimal getSub() {
        BigDecimal[] arr = {new BigDecimal("0.0001"), new BigDecimal("0.0002"), new BigDecimal("0.0003"), new BigDecimal("0.0004"), new BigDecimal("0.0005")};
        int index = (int) (Math.random() * arr.length);
        BigDecimal sub = arr[index];
        return sub;
    }

    /**
     * 保存并发出通知
     *
     * @param dataRedisKey   保存数据的redis key
     * @param updateRedisKey 通知的redis key
     * @param openTimeBegin  开始时间
     * @param openTimeEnd    结束时间
     * @param map            数据
     */
    public void saveAndNotify(String dataRedisKey, String updateRedisKey, Long openTimeBegin, Long openTimeEnd, Map<Long, List<KlineDataPo>> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        //删除旧数据
        templateDB5.opsForZSet().removeRangeByScore(dataRedisKey, openTimeBegin.doubleValue(), openTimeEnd.doubleValue());
        for (Long key : map.keySet()) {
            List<KlineDataPo> klineDataPos = map.get(key);
            for (KlineDataPo klineDataPo : klineDataPos) {
                String data = KlineUtil.kline2ArrayData(klineDataPo);
                //保存新数据
                double score = klineDataPo.getOpenTime().doubleValue();
                templateDB5.opsForZSet().add(dataRedisKey, data, score);
                //通知
                templateDB5.opsForZSet().add(updateRedisKey, score + "", score);
            }

        }


    }


}
