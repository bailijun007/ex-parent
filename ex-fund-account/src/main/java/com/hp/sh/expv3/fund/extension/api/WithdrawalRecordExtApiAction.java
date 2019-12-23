package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.DepositRecordExtErrorCode;
import com.hp.sh.expv3.fund.extension.constant.WithdrawalRecordExtErrorCode;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
@Api(tags = "体现记录扩展Api")
public class WithdrawalRecordExtApiAction implements WithdrawalRecordExtApi {
    @Autowired
    private WithdrawalRecordExtService withdrawalRecordExtService;

    @Autowired
    private WithdrawalAddrExtService withdrawalAddrExtService;

    @Override
    public List<WithdrawalRecordVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        if (userId == null || pageSize == null || StringUtils.isEmpty(asset)) {
            throw new ExException(WithdrawalRecordExtErrorCode.PARAM_EMPTY);
        }

        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, queryId, pageSize, pageStatus);

        return voList;
    }

    @Override
    public WithdrawalRecordVo queryLastHistory(Long userId, String asset) {
        WithdrawalRecordVo vo = withdrawalRecordExtService.queryLastHistory(userId, asset);
        return vo;
    }

    @Override
    public PageResult<WithdrawalRecordVo> queryAllUserHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<WithdrawalRecordVo> result = new PageResult<WithdrawalRecordVo>();
        if (pageNo == null || pageSize == null) {
            throw new ExException(WithdrawalRecordExtErrorCode.PARAM_EMPTY);
        }

        List<WithdrawalRecordVo> voList = getWithdrawalRecordVos(userId, asset, null, null, null);

        List<WithdrawalRecordVo> pageList = voList.stream().skip(pageSize * (pageNo - 1))
                .limit(pageSize)
                .collect(Collectors.toList());

        result.setList(pageList);
        Integer rowTotal = voList.size();
        result.setPageNo(pageNo);
        result.setRowTotal(new Long(rowTotal+ ""));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);

        return result;
    }

    private List<WithdrawalRecordVo> getWithdrawalRecordVos(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        List<WithdrawalRecordVo> voList = withdrawalRecordExtService.queryHistory(userId, asset, queryId, pageSize, pageStatus);
        List<WithdrawalAddrVo> withdrawalAddrVos = withdrawalAddrExtService.getAddressByUserIdAndAsset(userId, asset);
        if (!CollectionUtils.isEmpty(voList) && !CollectionUtils.isEmpty(withdrawalAddrVos)) {
            for (int i = 0; i < voList.size(); i++) {
                for (int j = 0; j < withdrawalAddrVos.size(); j++) {
                    if (isExsit(voList, withdrawalAddrVos, i, j)) {
                        voList.get(i).setTargetAddress(withdrawalAddrVos.get(j).getAddress());
                    }
                }
            }
        }

        return voList;
    }

    private boolean isExsit(List<WithdrawalRecordVo> voList, List<WithdrawalAddrVo> withdrawalAddrVos, int i, int j) {
        return voList.get(i).getUserId().equals(withdrawalAddrVos.get(j).getUserId())&&voList.get(i).getAsset().equals(withdrawalAddrVos.get(j).getAsset());
    }


}
