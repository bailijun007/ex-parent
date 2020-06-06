package com.hp.sh.expv3.service.impl;

import com.hp.sh.expv3.pojo.KlineDataPo;
import com.hp.sh.expv3.service.INewAssetDefaultKlineDataService;
import com.hp.sh.expv3.util.KlineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/6/4
 */
@Service
public class NewAssetDefaultKlineDataServiceImpl2 implements INewAssetDefaultKlineDataService {

    private static final Logger logger = LoggerFactory.getLogger(NewAssetDefaultKlineDataServiceImpl2.class);

    @Resource(name = "metadataTemplateDB5")
    private StringRedisTemplate templateDB5;

    @Autowired
    private NewAssetDefaultPriceService newAssetDefaultPriceService;

    @Override
    public void getDefaultKlineData(BigDecimal price, Long timestamp, String asset, String symbol) {
        long minusDay = 24 * 60 * 60 * 1000;
        long minusMin = 60 * 1000;
        Long start = timestamp - minusDay * 60;
        Long startInMin = start;
        List<BigDecimal> list = new ArrayList<>();
        List<Long> minTimeList = new ArrayList<>();

        for (int j = 0; j < 86400; j++) {
            startInMin = startInMin + minusMin;
            minTimeList.add(startInMin);
        }

        list = newAssetDefaultPriceService.buildPrice();
        list.add(price);
        minTimeList.add(timestamp);

        List<KlineDataPo> poList = new ArrayList<>();
        for (int j = 0; j < 86400; j++) {
            BigDecimal sub = getSub();
            KlineDataPo klineDataPo = new KlineDataPo();
            BigDecimal basePrice = list.get(j);
            klineDataPo.setOpenTime(minTimeList.get(j));
            klineDataPo.setOpen(basePrice);
            klineDataPo.setHigh(basePrice.add(sub));
            klineDataPo.setLow(basePrice.subtract(sub));
            klineDataPo.setClose(list.get(j + 1));
            BigDecimal volume = basePrice.multiply(new BigDecimal("" + (int) (Math.random() * 20)));
            klineDataPo.setVolume(volume.add(BigDecimal.TEN));
            poList.add(klineDataPo);
//            System.out.println(klineDataPo.getOpenTime()+","+klineDataPo.getOpen());
        }


        String dataRedisKey = "candle:bb:" + asset + ":" + symbol + ":" + 1;
        String updateRedisKey = "bb:kline:updateEvent:" + asset + ":" + symbol + ":" + 1;

        saveAndNotify(dataRedisKey, updateRedisKey, timestamp - minusDay * 60, timestamp, poList);

    }

    private BigDecimal getSub() {
        BigDecimal[] arr = {new BigDecimal("0.001"), new BigDecimal("0.0028"), new BigDecimal("0.0015"), new BigDecimal("0.002"), new BigDecimal("0.0023"), new BigDecimal("0.003"), new BigDecimal("0.0031")};
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
     * @param klineDataPos   数据
     */
    public void saveAndNotify(String dataRedisKey, String updateRedisKey, Long openTimeBegin, Long openTimeEnd, List<KlineDataPo> klineDataPos) {
        if (CollectionUtils.isEmpty(klineDataPos)) {
            return;
        }
        //删除旧数据
        templateDB5.opsForZSet().removeRangeByScore(dataRedisKey, openTimeBegin.doubleValue(), openTimeEnd.doubleValue());

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
