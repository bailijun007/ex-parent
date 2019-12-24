package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.constant.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcAccountSymbolExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountSymbolVO;
import com.hp.sh.expv3.pc.extension.vo.PcAccountSymbolVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@RestController
public class PcAccountSymbolExtendApiAction implements PcAccountSymbolExtendApi {
    @Autowired
    private PcAccountSymbolExtendService pcAccountSymbolExtendService;

    @Override
    public PcAccountSymbolVO query(Long userId, String asset, String symbol) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        PcAccountSymbolVO result = new PcAccountSymbolVO();

        PcAccountSymbolVo symbolVo = pcAccountSymbolExtendService.getPcAccountSymbol(userId, asset, symbol);
        if (null != symbolVo) {
            BeanUtils.copyProperties(symbolVo,result);
            //全仓最大杠杆 缺少字段，暂时用 全仓杠杆代替，后期更改
            result.setCrossMaxLeverage(symbolVo.getCrossLeverage());

            result.setBidMaxLeverage(symbolVo.getLongMaxLeverage());
            result.setAskMaxLeverage(symbolVo.getShortMaxLeverage());
            result.setBidLeverage(symbolVo.getLongLeverage());
            result.setAskLeverage(symbolVo.getShortLeverage());
        }

        return result;
    }
}
