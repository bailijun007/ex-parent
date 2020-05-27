package com.hp.sh.expv3.bb.trade.api;

import com.hp.sh.expv3.bb.extension.api.BbTradeApi;
import com.hp.sh.expv3.bb.extension.error.BbCommonErrorCode;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.trade.service.BbTradeExtendService;
import com.hp.sh.expv3.bb.trade.util.CommonDateUtils;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@RestController
public class BbTradeExtendApiAction implements BbTradeApi {
    @Autowired
    private BbTradeExtendService bbTradeExtService;


    @Override
    public List<BbTradeVo> queryLastTrade(String asset, String symbol, Integer count) {
        checkParam(asset, symbol, count);
        String[] startAndEndTime = CommonDateUtils.getStartAndEndTime(null, null);
        String startTime = startAndEndTime[0];
        String endTime = startAndEndTime[1];
        List<BbTradeVo> list = bbTradeExtService.queryTradeResult(asset, symbol, count,startTime,endTime);

        return list;
    }


    @Override
    public List<BbTradeVo> queryTradeList(String asset, String symbol, Long userId, Integer count) {
        if (userId == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
        }
        if (count > 100) {
            throw new ExException(BbCommonErrorCode.MORE_THAN_MAX_ROW);
        }
        String[] startAndEndTime = CommonDateUtils.getStartAndEndTime(null, null);
        String startTime = startAndEndTime[0];
        String endTime = startAndEndTime[1];
        return bbTradeExtService.queryTradeList(userId, asset, symbol, count, startTime, endTime);
    }

    @Override
    public List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime) {
        if (null == startTime || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
        }
        return bbTradeExtService.selectTradeListByTimeInterval(asset, symbol, startTime, endTime);
    }

    //    @Override
    public List<BbTradeVo> selectTradeListByUser(String asset, String symbol, Long userId, Long startTime, Long endTime) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
        }

        if (startTime == null && endTime == null) {
            endTime = Instant.now().toEpochMilli();
            startTime = endTime - ((endTime + TimeZone.getDefault().getRawOffset()) % (24 * 60 * 60 * 1000L));
        }
        return bbTradeExtService.selectTradeListByUser(userId, asset, symbol, startTime, endTime);
    }

    @Override
    public BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long startTime) {
        if (null == startTime || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
        }
        return bbTradeExtService.queryLastTradeByLtTime(asset, symbol, startTime);
    }
    private void checkParam(String asset, String symbol, Integer count) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == count) {
            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
        }
        if (count > 100) {
            throw new ExException(BbCommonErrorCode.MORE_THAN_MAX_ROW);
        }
    }

}
