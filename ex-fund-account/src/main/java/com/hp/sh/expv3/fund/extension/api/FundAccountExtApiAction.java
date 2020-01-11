package com.hp.sh.expv3.fund.extension.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.error.FundCommonError;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendService;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 资金账户相关请求
 *
 * @author BaiLiJun  on 2019/12/13
 */
@RestController
public class FundAccountExtApiAction implements FundAccountExtApi {
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
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }
        CapitalAccountVo capitalAccount = fundAccountExtendServer.getCapitalAccount(userId, asset);
        if (null == capitalAccount) {
            throw new ExException(FundCommonError.ACCOUNT_NOT_FIND);
        }

        //查询冻结资金
        BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(userId, asset);
        capitalAccount.setLock(frozenCapital);
        capitalAccount.setTotalAssets(frozenCapital.add(capitalAccount.getAvailable()));
        return capitalAccount;
    }

    @Override
    public PageResult<CapitalAccountVo> findFundAccountList(Long userId, String asset, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(FundCommonError.PARAM_EMPTY);
        }

        PageResult<CapitalAccountVo> result = fundAccountExtendServer.pageQueryAccountList(userId, asset,  pageNo,  pageSize);
        if (!CollectionUtils.isEmpty(result.getList())) {
            for (CapitalAccountVo vo : result.getList()) {
                //查询冻结资金
                BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(vo.getAccountId(), vo.getAsset());
                vo.setLock(frozenCapital);
                vo.setTotalAssets(frozenCapital.add(vo.getAvailable()));
            }
        }

        return result;
    }


}
