package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.DepositRecordExtErrorCode;
import com.hp.sh.expv3.fund.extension.constant.FundAccountExtErrorCode;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * 资金账户相关请求
 *
 * @author BaiLiJun  on 2019/12/13
 */
@RestController
public class CapitalAccountExtApiAction implements CapitalAccountExtApi {
    @Autowired
    private FundAccountExtendService fundAccountExtendServer;

    @Autowired
    private WithdrawalRecordExtService withdrawalRecordExtServer;

    @Override
    @ApiOperation("获取资金账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC")
    })
    public CapitalAccountVo getCapitalAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset) {
        if (userId == null) {
            throw new ExException(FundAccountExtErrorCode.PARAM_EMPTY);
        }
        CapitalAccountVo capitalAccount = fundAccountExtendServer.getCapitalAccount(userId, asset);
        if (null == capitalAccount) {
            throw new ExException(DepositRecordExtErrorCode.ACCOUNT_NOT_FIND);
        }

        //查询冻结资金
        BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(userId, asset);
        capitalAccount.setLock(frozenCapital);
        capitalAccount.setTotalAssets(frozenCapital.add(capitalAccount.getAvailable()));
        return capitalAccount;
    }


}
