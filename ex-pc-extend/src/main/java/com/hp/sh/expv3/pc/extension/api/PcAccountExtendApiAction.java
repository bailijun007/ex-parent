/**
 *
 */
package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.service.impl.PcAccountExtendServiceImpl;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;

/**
 * 永续合约账户扩展接口
 *
 * @author wangjg
 */
@RestController
public class PcAccountExtendApiAction implements PcAccountExtendApi {

    @Autowired
    private PcAccountExtendServiceImpl pcAccountExtendService;

    @Autowired
    private PcOrderExtendService pcOrderExtendService;

    @Autowired
    private PcPositionExtendService pcPositionExtendService;

    @Override
    public BigDecimal getBalance(Long userId, String asset) {
        return pcAccountExtendService.getBalance(userId, asset);
    }

    @Override
    public List<PcAccountExtVo> findContractAccount(String userIds, String asset) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(userIds)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcAccountExtVo> list = new ArrayList<>();
        String[] split = userIds.split(",");
        for (int i = 0; i < split.length; i++) {
            long userId = Long.parseLong(split[i]);
            PcAccountExtVo vo = pcAccountExtendService.findContractAccount(userId, asset);
            if (null == vo) {
                vo.setTotal(BigDecimal.ZERO);
                vo.setPoserMargin(BigDecimal.ZERO);
                vo.setOrderMargin(BigDecimal.ZERO);
                vo.setAvailable(BigDecimal.ZERO);
                vo.setAsset(asset);
                vo.setAccountId(userId);
            } else {
                BigDecimal orderMargin = pcOrderExtendService.getGrossMargin(userId, asset);
                vo.setOrderMargin(orderMargin);
                BigDecimal posMargin = pcPositionExtendService.getPosMargin(userId, asset);
                vo.setPoserMargin(posMargin);
                vo.setTotal(vo.getAvailable().add(orderMargin).add(posMargin));
            }
            list.add(vo);
        }

        return list;
    }

    @Override
    public PageResult<PcAccountExtVo> findContractAccountList(Long userId, String asset, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        PageResult<PcAccountExtVo> result = pcAccountExtendService.pageQueryContractAccountList(userId, asset, pageNo, pageSize);
        if (!CollectionUtils.isEmpty(result.getList())) {
            for (PcAccountExtVo vo : result.getList()) {
                BigDecimal orderMargin = pcOrderExtendService.getGrossMargin(vo.getAccountId(), vo.getAsset());
                vo.setOrderMargin(orderMargin);
                BigDecimal posMargin = pcPositionExtendService.getPosMargin(vo.getAccountId(), vo.getAsset());
                vo.setPoserMargin(posMargin);
                vo.setTotal(vo.getAvailable().add(orderMargin).add(posMargin));
            }
        }
        return result;
    }

}
