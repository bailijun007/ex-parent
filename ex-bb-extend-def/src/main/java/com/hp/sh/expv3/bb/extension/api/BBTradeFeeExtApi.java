package com.hp.sh.expv3.bb.extension.api;

import java.math.BigDecimal;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@Api(tags = "手续费")
@FeignClient(value = "ex-bb-extend")
public interface BBTradeFeeExtApi {

    @ApiOperation(value = "查询用户手续费)")
    @GetMapping(value = "/api/bb/trade/ext/fee/query")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId ", value = "用户id", example = "1", required = true),
        @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
        @ApiImplicitParam(name = "makerFlag", value = "是否maker", required = false),
        @ApiImplicitParam(name = "beginTime ", value = "开始时间", example = "1", required = true),
        @ApiImplicitParam(name = "endTime ", value = "结束时间", example = "1", required = true),
    })
    public BigDecimal query(Long userId, String asset, Integer makerFlag, Long beginTime, Long endTime);

}
