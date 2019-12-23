/**
 *
 */
package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.extension.service.impl.PcAccountExtendServiceImpl;

/**
 * 永续合约账户扩展接口
 *
 * @author wangjg
 */
@RestController
public class PcAccountExtendApiAction implements PcAccountExtendApi {

    @Autowired
    private PcAccountExtendServiceImpl pcAccountCoreService;

    @Autowired
    private PcOrderExtendService pcOrderCoreService;

    @Autowired
    private PcPositionExtendService pcPositionCoreService;

    @Override
    public BigDecimal getBalance(Long userId, String asset) {
        return pcAccountCoreService.getBalance(userId, asset);
    }

    @Override
    public List<PcAccountExtVo> findContractAccount(String userIds, String asset) {
        List<PcAccountExtVo> list = new ArrayList<>();
        String[] split = userIds.split(",");
        for (int i = 0; i < split.length; i++) {
            PcAccountExtVo vo = pcAccountCoreService.findContractAccount(Long.parseLong(split[i]), asset);
            BigDecimal orderMargin = pcOrderCoreService.getGrossMargin(Long.parseLong(split[i]), asset);
            vo.setOrderMargin(orderMargin);
            BigDecimal posMargin = pcPositionCoreService.getPosMargin(Long.parseLong(split[i]), asset);
            vo.setPoserMargin(posMargin);

            vo.setTotal(vo.getAvailable().add(orderMargin).add(posMargin));
            list.add(vo);
        }

        return list;
    }

}
