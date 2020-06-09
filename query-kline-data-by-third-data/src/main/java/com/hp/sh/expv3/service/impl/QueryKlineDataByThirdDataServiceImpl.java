package com.hp.sh.expv3.service.impl;


import com.hp.sh.expv3.controller.QueryKlineDataByThirdDataController;
import com.hp.sh.expv3.mapper.KlineDataMapper;
import com.hp.sh.expv3.pojo.KlineDataPo;
import com.hp.sh.expv3.service.IQueryKlineDataByThirdDataService;
import com.hp.sh.expv3.util.KlineUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/6
 */
@Service
public class QueryKlineDataByThirdDataServiceImpl implements IQueryKlineDataByThirdDataService {

    private static final Logger logger = LoggerFactory.getLogger(QueryKlineDataByThirdDataServiceImpl.class);


    @Autowired
    private KlineDataMapper klineDataMapper;

//    @Autowired
//    @Qualifier("metadataDb5RedisUtil")
//    private RedisUtil metadataDb5RedisUtil;

    @Resource(name = "metadataTemplateDB5")
    private StringRedisTemplate templateDB5;


    /**
     * 获取第三方k线数据，
     * 这里取zb 或 binance 交易所数据（可修改）
     *
     * @param tableName
     * @param klineType
     * @param pair
     * @param interval
     * @param openTimeBegin
     * @param openTimeEnd
     */
    @Override
    public void queryKlineDataByThirdData(String tableName, Integer klineType, String asset, String pair, String interval, Long openTimeBegin, Long openTimeEnd) {
        String expName = "binance";
        List<KlineDataPo> klineDataPos = null;
        if (pair.equals("BYM_USDT")) {
            String pair2 = "ETH_USDT";
            klineDataPos = klineDataMapper.queryKlineDataByThirdData(tableName, klineType, pair2, interval, openTimeBegin, openTimeEnd, expName);
            if (CollectionUtils.isEmpty(klineDataPos)) {
                expName = "bitfinex";
                klineDataPos = klineDataMapper.queryKlineDataByThirdData(tableName, klineType, pair2, interval, openTimeBegin, openTimeEnd, expName);
                for (KlineDataPo klineDataPo : klineDataPos) {
                    klineDataPo.setOpen(klineDataPo.getOpen().divide(new BigDecimal("1500"), 8, RoundingMode.DOWN));
                    klineDataPo.setHigh(klineDataPo.getHigh().divide(new BigDecimal("1500"), 8, RoundingMode.DOWN));
                    klineDataPo.setLow(klineDataPo.getLow().divide(new BigDecimal("1500"), 8, RoundingMode.DOWN));
                    klineDataPo.setClose(klineDataPo.getClose().divide(new BigDecimal("1500"), 8, RoundingMode.DOWN));
                    klineDataPo.setVolume(klineDataPo.getVolume().divide(BigDecimal.TEN, 8, RoundingMode.DOWN));
                }
            }
        } else {
            klineDataPos = klineDataMapper.queryKlineDataByThirdData(tableName, klineType, pair, interval, openTimeBegin, openTimeEnd, expName);
            if (CollectionUtils.isEmpty(klineDataPos)) {
                expName = "bitfinex";
                klineDataPos = klineDataMapper.queryKlineDataByThirdData(tableName, klineType, pair, interval, openTimeBegin, openTimeEnd, expName);
            }
        }

        logger.info("成功修复pair={} k线数据，数量为：{}", pair, klineDataPos.size());
        if (CollectionUtils.isEmpty(klineDataPos)) {
            return;
        }
        String dataRedisKey = null;
        String updateRedisKey = null;
        if (klineType == 1) {
            dataRedisKey = "candle:bb:" + asset + ":" + pair + ":" + 1;
            updateRedisKey = "bb:kline:updateEvent:" + asset + ":" + pair + ":" + 1;
        } else if (klineType == 2) {
            dataRedisKey = "candle:pc:" + asset + ":" + pair + ":" + 1;
            updateRedisKey = "pc:kline:updateEvent:" + asset + ":" + pair + ":" + 1;
        }
        saveAndNotify(dataRedisKey, updateRedisKey, openTimeBegin, openTimeEnd, klineDataPos);
        logger.info("修复完成");
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
