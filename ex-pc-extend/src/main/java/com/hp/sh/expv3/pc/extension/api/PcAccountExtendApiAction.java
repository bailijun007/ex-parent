/**
 *
 */
package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
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
            PcAccountExtVo vo = pcAccountExtendService.findContractAccount(Long.parseLong(split[i]), asset);
            BigDecimal orderMargin = pcOrderExtendService.getGrossMargin(Long.parseLong(split[i]), asset);
            vo.setOrderMargin(orderMargin);
            BigDecimal posMargin = pcPositionExtendService.getPosMargin(Long.parseLong(split[i]), asset);
            vo.setPoserMargin(posMargin);

            vo.setTotal(vo.getAvailable().add(orderMargin).add(posMargin));
            list.add(vo);
        }

        return list;
    }

    @Override
    public PageResult<PcAccountExtVo> findContractAccountList(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<PcAccountExtVo> result=new PageResult();
        List<PcAccountExtVo> list = pcAccountExtendService.findContractAccountList(userId, asset);
        if (!CollectionUtils.isEmpty(list)) {
            for (PcAccountExtVo vo : list) {
                BigDecimal orderMargin = pcOrderExtendService.getGrossMargin(vo.getAccountId(), vo.getAsset());
                vo.setOrderMargin(orderMargin);
                BigDecimal posMargin = pcPositionExtendService.getPosMargin(vo.getAccountId(), vo.getAsset());
                vo.setPoserMargin(posMargin);
                vo.setTotal(vo.getAvailable().add(orderMargin).add(posMargin));
            }

            List<PcAccountExtVo> voList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
            result.setList(voList);
        }
        Integer rowTotal = list.size();
        result.setPageNo(pageNo);
        result.setRowTotal(new Long(rowTotal+""));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);

        return result;
    }

}
