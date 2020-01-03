package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.service.PcTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
    public PcTradeVo queryLastTradeByGtTime(String asset, String symbol, Long startTime) {
        if (null == startTime || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        PcTradeVo pcTradeVo = pcTradeExtendService.queryLastTrade(asset, symbol, startTime,null);

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
