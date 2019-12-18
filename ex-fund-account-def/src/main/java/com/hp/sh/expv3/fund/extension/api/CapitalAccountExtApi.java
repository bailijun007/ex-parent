package com.hp.sh.expv3.fund.extension.api;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 资金账户相关请求
 *
 * @author BaiLiJun  on 2019/12/13
 */
@Api(tags ="资金账户扩展Api")
@FeignClient(value = "ex-fund-account")
public interface CapitalAccountExtApi {

    @ApiOperation("获取资金账户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id",example = "1",required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型",example = "BTC")
    })
    @GetMapping(value = "/api/extension/account/total/query")
    public CapitalAccountVo getCapitalAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);


}
