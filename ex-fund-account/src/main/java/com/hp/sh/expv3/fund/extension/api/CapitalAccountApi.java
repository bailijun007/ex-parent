package com.hp.sh.expv3.fund.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.CapitalAccountErrorCode;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendServer;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtServer;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 资金账户相关请求
 *
 * @author BaiLiJun  on 2019/12/13
 */
@RestController
@Api(tags ="资金账户相关请求")
@RequestMapping("/baseUrl/account/total")
public class CapitalAccountApi {
    @Autowired
    private FundAccountExtendServer fundAccountExtendServer;

    @Autowired
    private WithdrawalRecordExtServer withdrawalRecordExtServer;

    @ApiOperation("获取资金账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id",example = "1"),
            @ApiImplicitParam(name = "asset", value = "资产类型",example = "BTC")
    })
    @GetMapping(value = "/query")
    public CapitalAccountVo getCapitalAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset) {
        if (userId == null) {
            throw new ExException(CapitalAccountErrorCode.PARAM_EMPTY);
        }
        CapitalAccountVo capitalAccount = fundAccountExtendServer.getCapitalAccount(userId, asset);

        //查询冻结资金
        BigDecimal frozenCapital = withdrawalRecordExtServer.getFrozenCapital(userId, asset);

        capitalAccount.setLock(frozenCapital);
        capitalAccount.setTotalAssets(frozenCapital.add(capitalAccount.getAvailable()));
        return capitalAccount;
    }


}
