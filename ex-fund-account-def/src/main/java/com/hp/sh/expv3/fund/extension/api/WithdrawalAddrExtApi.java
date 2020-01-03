package com.hp.sh.expv3.fund.extension.api;

import java.util.List;

import com.gitee.hupadev.base.api.PageResult;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Api(tags ="提币地址扩展Api")
@FeignClient(value="ex-fund-account")
public interface WithdrawalAddrExtApi {

    @ApiOperation("查询提币地址")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId",value = "用户id"),
            @ApiImplicitParam(name = "asset",value = "资产"),
            @ApiImplicitParam(name = "pageNo",value = "当前页"),
            @ApiImplicitParam(name = "pageSize",value = "每页显示多少条"),
            @ApiImplicitParam(name = "enabled",value = "1:启用/0:禁用")
    })
    @GetMapping(value = "/api/extension/account/withdraw/address/query")
    public PageResult<WithdrawalAddrVo> findWithdrawalAddr(@RequestParam("userId") Long userId,
                                                           @RequestParam("asset") String asset,
                                                           @RequestParam("pageNo") Long pageNo,
                                                           @RequestParam("pageSize") Integer pageSize,
                                                           @RequestParam("enabled") Integer enabled);


}
