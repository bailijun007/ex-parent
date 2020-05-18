package com.hp.sh.expv3.bb.extension.service.impl;


import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.IQueryKlineDataByThirdDataService;
import com.hp.sh.expv3.bb.extension.thirdKlineData.mapper.ThirdKlineDataMapper;
import com.hp.sh.expv3.bb.extension.util.KlineUtil;
import com.hp.sh.expv3.bb.extension.vo.KlineDataPo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/6
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class QueryKlineDataByThirdDataServiceImpl implements IQueryKlineDataByThirdDataService {

    @Autowired
    private ThirdKlineDataMapper klineDataMapper;

    @Resource(name = "templateDB5")
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
        //判断表是否存在
        String dbName = "ted";
        int i = klineDataMapper.existTable(dbName, tableName);
        if (i != 1) {
            throw new ExException(BbExtCommonErrorCode.TABLE_NOT_EXIST);
        }
        String expName = "zb";
        List<KlineDataPo> klineDataPos = klineDataMapper.queryKlineDataByThirdData(tableName, klineType, pair, interval, openTimeBegin, openTimeEnd, expName);
        if (CollectionUtils.isEmpty(klineDataPos)) {
            expName = "binance";
            klineDataPos = klineDataMapper.queryKlineDataByThirdData(tableName, klineType, pair, interval, openTimeBegin, openTimeEnd, expName);
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
