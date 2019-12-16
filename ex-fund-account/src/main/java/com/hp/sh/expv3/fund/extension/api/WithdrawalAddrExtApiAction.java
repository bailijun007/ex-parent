package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.DepositRecordExtErrorCode;
import com.hp.sh.expv3.fund.extension.constant.WithdrawalAddressExtErrorCode;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
public class WithdrawalAddrExtApiAction implements  WithdrawalAddrExtApi{
   @Autowired
   private WithdrawalAddrExtService withdrawalAddrExtService;

    @Override
    public List<WithdrawalAddrVo> findWithdrawalAddr(@RequestBody WithdrawalAddrParam param) {
        if (param.getUserId()==null || param.getPageNo() == null || param.getPageSize() == null|| StringUtils.isEmpty(param.getAsset())) {
            throw new ExException(WithdrawalAddressExtErrorCode.PARAM_EMPTY);
        }

        List<WithdrawalAddrVo> addrVoList = withdrawalAddrExtService.findWithdrawalAddr(param.getUserId(), param.getAsset());
        if(CollectionUtils.isEmpty(addrVoList)){
            throw new ExException(WithdrawalAddressExtErrorCode.DATA_EMPTY);
        }
        //分页
        List<WithdrawalAddrVo> voList = addrVoList.stream().skip(param.getPageSize() * (param.getPageNo() - 1))
                .limit(param.getPageSize()).collect(Collectors.toList());
        return voList;
    }
}
