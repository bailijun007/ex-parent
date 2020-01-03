package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@RestController
public class WithdrawalAddrExtApiAction implements  WithdrawalAddrExtApi{
   @Autowired
   private WithdrawalAddrExtService withdrawalAddrExtService;


    @Override
    public PageResult<WithdrawalAddrVo> findWithdrawalAddr(Long userId, String asset, Integer pageNo, Integer pageSize, Integer enabled) {
        if (userId==null || pageNo == null || pageSize == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        PageResult<WithdrawalAddrVo> result = withdrawalAddrExtService.pageQueryWithdrawalAddrList(userId, asset,pageNo,pageSize,enabled);
        return result;
    }
}
