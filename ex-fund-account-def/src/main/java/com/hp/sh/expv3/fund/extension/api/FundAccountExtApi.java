package com.hp.sh.expv3.fund.extension.api;


import com.gitee.hupadev.base.api.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.math.BigDecimal;
import java.util.List;

/**
 * 资金账户相关请求
 *
 * @author BaiLiJun  on 2019/12/13
 */
@Api(tags = "资金账户扩展Api")
@FeignClient(value = "ex-fund-account")
public interface FundAccountExtApi {

    @ApiOperation("获取资金账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC")
    })
    @GetMapping(value = "/api/extension/account/total/query")
    public CapitalAccountVo getCapitalAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);


    @ApiOperation("资金账户列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/total/findFundAccountList")
    public PageResult<CapitalAccountVo> findFundAccountList(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                            @RequestParam(value = "pageNo", required = true) Integer pageNo, @RequestParam(value = "pageSize", required = true,defaultValue = "20") Integer pageSize);



    @ApiOperation("根据币种查询平台所有用户的资金账户总额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true)
    })
    @GetMapping(value = "/api/extension/fundAccount/queryTotalNumber")
    public BigDecimal queryTotalNumber(@RequestParam(value = "asset") String asset);
}
