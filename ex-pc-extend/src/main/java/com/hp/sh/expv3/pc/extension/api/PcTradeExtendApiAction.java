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

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@RestController
public class PcTradeExtendApiAction implements PcTradeExtendApi{
    @Autowired
    private PcTradeExtendService pcTradeExtendService;


    @Override
    public List<PcTradeVo> queryLastTrade(String asset, String symbol, Integer count) {
        checkParam(asset, symbol, count);

        List<PcTradeVo> list = pcTradeExtendService.queryTradeResult(asset, symbol, count);

        return list;
    }

    private void checkParam(String asset, String symbol, Integer count) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == count ) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        if(count>100){
            throw new ExException(PcCommonErrorCode.MORE_THAN_MAX_ROW);

        }
    }

}
