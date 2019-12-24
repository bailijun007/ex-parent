package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.constant.PcPositionErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@RestController
public class PcOrderTradeExtendApiAction implements PcOrderTradeExtendApi {
    @Autowired
    private PcOrderTradeService pcOrderTradeService;


    @Override
    public List<PcOrderTradeDetailVo> query(Long userId, String asset, String symbol, String orderId) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId || StringUtils.isEmpty(orderId)) {
            throw new ExException(PcPositionErrorCode.PARAM_EMPTY);
        }
        List<PcOrderTradeDetailVo> result=new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.queryOrderTrade(userId, asset, symbol, orderId);
        if (!CollectionUtils.isEmpty(voList)) {
            for (PcOrderTradeVo pcOrderTradeVo : voList) {
                PcOrderTradeDetailVo detailVo = new PcOrderTradeDetailVo();
                BeanUtils.copyProperties(pcOrderTradeVo,detailVo);
                detailVo.setQty(pcOrderTradeVo.getVolume());
                detailVo.setAmt(pcOrderTradeVo.getPrice().multiply(pcOrderTradeVo.getVolume()));
                result.add(detailVo);
            }
        }

        return result;
    }
}
