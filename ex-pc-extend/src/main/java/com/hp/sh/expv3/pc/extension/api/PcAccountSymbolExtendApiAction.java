package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.PcDefaultSymbolSetting;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcAccountSymbolExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountSettingVo;
import com.hp.sh.expv3.pc.extension.vo.PcAccountSymbolVo;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@RestController
public class PcAccountSymbolExtendApiAction implements PcAccountSymbolExtendApi {
    @Autowired
    private PcAccountSymbolExtendService pcAccountSymbolExtendService;
    
	@Autowired
	private MetadataService metadataService;
	@Autowired
	private PcDefaultSymbolSetting defaultSymbolSetting;

    @Override
    public PcAccountSettingVo query(Long userId, String asset, String symbol) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        PcAccountSettingVo result = new PcAccountSettingVo();
        
        result.setAsset(asset);
        result.setSymbol(symbol);

        PcAccountSymbolVo symbolVo = pcAccountSymbolExtendService.getPcAccountSymbol(userId, asset, symbol);
        if (null != symbolVo) {
            BeanUtils.copyProperties(symbolVo,result);
            //全仓最大杠杆 缺少字段，暂时用 全仓杠杆代替，后期更改
            result.setCrossMaxLeverage(symbolVo.getCrossLeverage());

            result.setBidMaxLeverage(symbolVo.getLongMaxLeverage());
            result.setAskMaxLeverage(symbolVo.getShortMaxLeverage());
            result.setBidLeverage(symbolVo.getLongLeverage());
            result.setAskLeverage(symbolVo.getShortLeverage());
        }else {
        	BigDecimal maxLeverage = metadataService.getMaxLeverage(userId, asset, symbol, BigDecimal.ZERO);
        	if(maxLeverage==null){
        		maxLeverage = new BigDecimal(49);
        	}
        	
            result.setCrossMaxLeverage(maxLeverage);

            result.setBidMaxLeverage(maxLeverage);
            result.setAskMaxLeverage(maxLeverage);
            result.setBidLeverage(defaultSymbolSetting.getLongLeverage(asset, symbol));
            result.setAskLeverage(defaultSymbolSetting.getShortLeverage(asset, symbol));
            result.setCrossLeverage(defaultSymbolSetting.getCrossLeverage(asset, symbol));
            result.setMarginMode(defaultSymbolSetting.getMarginMode());
        	
        }

        return result;
    }
}
