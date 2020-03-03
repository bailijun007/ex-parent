package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.TimeZone;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@RestController
public class BbTradeExtApiAction implements BbTradeExtApi {
   @Autowired
    private BbTradeExtService bbTradeExtService;

    @Override
    public List<BbTradeVo> queryTradeList(String asset, String symbol, Long userId,Integer count) {
        if (userId == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        if (count > 100) {
            throw new ExException(BbExtCommonErrorCode.MORE_THAN_MAX_ROW);
        }
        return bbTradeExtService.queryTradeList(userId,asset,symbol,count);
    }

    @Override
    public List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime) {
        if (null == startTime || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbTradeExtService.selectTradeListByTimeInterval(asset,symbol,startTime,endTime);
    }

    @Override
    public List<BbTradeVo> selectTradeListByUser(String asset, String symbol, Long userId, Long startTime, Long endTime) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }

        if (startTime == null && endTime == null) {
            endTime = Instant.now().toEpochMilli();
            startTime = endTime - ((endTime + TimeZone.getDefault().getRawOffset()) % (24 * 60 * 60 * 1000L));
        }
        return bbTradeExtService.selectTradeListByUser(userId,asset, symbol, startTime, endTime);
    }

    @Override
    public BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long startTime) {
        if (null == startTime || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbTradeExtService.queryLastTradeByLtTime(asset,symbol,startTime);
    }

    @Override
    public List<BbTradeVo> queryLastTrade(String asset, String symbol, Integer count) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == count) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        if (count > 100) {
            throw new ExException(BbExtCommonErrorCode.MORE_THAN_MAX_ROW);
        }
        return bbTradeExtService.queryLastTrade(asset,symbol,count);
    }
}
