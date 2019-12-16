package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.DepositRecordExtErrorCode;
import com.hp.sh.expv3.fund.extension.constant.WithdrawalRecordExtErrorCode;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
        if (userId == null || pageSize == null || queryId == null || StringUtils.isEmpty(asset)) {
            throw new ExException(WithdrawalRecordExtErrorCode.PARAM_EMPTY);
        }

        List<WithdrawalRecordVo> voList = withdrawalRecordExtService.queryHistory(userId, asset, queryId, pageSize, pageStatus);

        String addr = withdrawalAddrExtService.getAddressByUserIdAndAsset(userId, asset);
        for (WithdrawalRecordVo vo : voList) {
            vo.setTargetAddress(addr);
        }

        return voList;
    }
}
