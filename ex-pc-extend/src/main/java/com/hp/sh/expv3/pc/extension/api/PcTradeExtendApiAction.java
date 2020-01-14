package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.TimeZone;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@RestController
public class PcTradeExtendApiAction implements PcTradeExtendApi {
    @Autowired
    private PcTradeExtendService pcTradeExtendService;


    @Override
    public List<PcTradeVo> queryLastTrade(String asset, String symbol, Integer count) {
        checkParam(asset, symbol, count);

        List<PcTradeVo> list = pcTradeExtendService.queryTradeResult(asset, symbol, count);

        return list;
    }

    @Override
    public List<PcTradeVo> queryTradeByGtTime(String asset, String symbol, Integer type, Long startTime) {
        if (null == startTime || type == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcTradeVo> list = pcTradeExtendService.queryTradeByGtTime(asset, symbol, startTime, null, type);
        return list;
    }

    @Override
    public PcTradeVo queryLastTradeByLtTime(String asset, String symbol, Long startTime) {
        if (null == startTime || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        PcTradeVo pcTradeVo = pcTradeExtendService.queryLastTrade(asset, symbol, startTime);

        return pcTradeVo;
    }

    /**
     * 通过一个时间区间获取数据包含开始时间，升序排列
     *
     * @param asset     资产
     * @param symbol    交易对
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    @Override
    public List<PcTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime) {
        if (null == startTime || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcTradeVo> pcTradeVo = pcTradeExtendService.selectTradeListByTimeInterval(asset, symbol, startTime, endTime, null);
        return pcTradeVo;
    }

    @Override
    public List<PcTradeVo> selectTradeListByUser(String asset, String symbol, Long userId, Long startTime, Long endTime) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        if (startTime == null && endTime == null) {
            endTime = Instant.now().toEpochMilli();
            startTime = endTime - ((endTime + TimeZone.getDefault().getRawOffset()) % (24 * 60 * 60 * 1000L));
        }

        List<PcTradeVo> pcTradeVo = pcTradeExtendService.selectTradeListByTimeInterval(asset, symbol, startTime, endTime, userId);
        return pcTradeVo;
    }

    @Override
    public List<PcTradeVo> selectTradeListByUserId(String asset, String symbol, Long userId, Long startTime, Long endTime) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null ) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        if (startTime == null && endTime == null) {
            endTime = Instant.now().toEpochMilli();
        }

        List<PcTradeVo> pcTradeVo = pcTradeExtendService.selectTradeListByUserId(asset, symbol, startTime, endTime, userId);
        return pcTradeVo;
    }

    @Override
    public BigDecimal getTotalTurnover(String asset, String symbol, Long startTime, Long endTime) {
        if (null == startTime || endTime == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcTradeVo> list = pcTradeExtendService.queryTradeByGtTime(asset, symbol, startTime, endTime, null);
        BigDecimal total = list.stream().map(PcTradeVo::getNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
        return total;
    }

    private void checkParam(String asset, String symbol, Integer count) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == count) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        if (count > 100) {
            throw new ExException(PcCommonErrorCode.MORE_THAN_MAX_ROW);
        }
    }

}
