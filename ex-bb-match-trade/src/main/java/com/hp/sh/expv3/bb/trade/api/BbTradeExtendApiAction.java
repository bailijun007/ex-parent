package com.hp.sh.expv3.bb.trade.api;

import com.hp.sh.expv3.bb.extension.api.BbTradeApi;
import com.hp.sh.expv3.bb.extension.error.BbCommonErrorCode;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.trade.service.BbTradeExtendService;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@RestController
public class BbTradeExtendApiAction implements BbTradeApi {
    @Autowired
    private BbTradeExtendService bbTradeExtendService;


    @Override
    public List<BbTradeVo> queryLastTrade(String asset, String symbol, Integer count) {
        checkParam(asset, symbol, count);

        List<BbTradeVo> list = bbTradeExtendService.queryTradeResult(asset, symbol, count);

        return list;
    }

//    @Override
//    public List<BbTradeVo> queryTradeByGtTime(String asset, String symbol, Integer type, Long startTime) {
//        if (null == startTime || type == null) {
//            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
//        }
//        List<BbTradeVo> list = bbTradeExtendService.queryTradeByGtTime(asset, symbol, startTime, null, type);
//        return list;
//    }
//
//    @Override
//    public BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long startTime) {
//        if (null == startTime || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
//            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
//        }
//        BbTradeVo pcTradeVo = bbTradeExtendService.queryLastTrade(asset, symbol, startTime);
//
//        return pcTradeVo;
//    }
//
//    /**
//     * 通过一个时间区间获取数据包含开始时间，升序排列
//     *
//     * @param asset     资产
//     * @param symbol    交易对
//     * @param startTime 开始时间
//     * @param endTime   结束时间
//     * @return
//     */
//    @Override
//    public List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime) {
//        if (null == startTime || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
//            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
//        }
//        List<BbTradeVo> pcTradeVo = bbTradeExtendService.selectTradeListByTimeInterval(asset, symbol, startTime, endTime, null);
//        return pcTradeVo;
//    }
//
//    @Override
//    public List<BbTradeVo> selectTradeListByUser(String asset, String symbol, Long userId, Long startTime, Long endTime) {
//        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
//            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
//        }
//
//        if (startTime == null && endTime == null) {
//            endTime = Instant.now().toEpochMilli();
//            startTime = endTime - ((endTime + TimeZone.getDefault().getRawOffset()) % (24 * 60 * 60 * 1000L));
//        }
//
//        List<BbTradeVo> pcTradeVo = bbTradeExtendService.selectTradeListByTimeInterval(asset, symbol, startTime, endTime, userId);
//        return pcTradeVo;
//    }
//
//    @Override
//    public BigDecimal getTotalTurnover(String asset, String symbol, Long startTime, Long endTime) {
//        if (null == startTime || endTime == null) {
//            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
//        }
//        List<BbTradeVo> list = bbTradeExtendService.queryTradeByGtTime(asset, symbol, startTime, endTime, null);
//        BigDecimal total = list.stream().map(BbTradeVo::getNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
//        return total;
//    }

    private void checkParam(String asset, String symbol, Integer count) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == count) {
            throw new ExException(BbCommonErrorCode.PARAM_EMPTY);
        }
        if (count > 100) {
            throw new ExException(BbCommonErrorCode.MORE_THAN_MAX_ROW);
        }
    }

}
