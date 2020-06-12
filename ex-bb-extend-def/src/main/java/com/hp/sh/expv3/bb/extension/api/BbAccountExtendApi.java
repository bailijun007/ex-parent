/**
 *
 */
package com.hp.sh.expv3.bb.extension.api;

import java.math.BigDecimal;

import com.hp.sh.expv3.bb.extension.vo.BbAccountExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author bailj
 */
@Api(tags = "用户bb资金账户扩展接口")
@FeignClient(value = "ex-bb-extend")
public interface BbAccountExtendApi {

    @ApiOperation(value = "创建BB账号")
    @GetMapping(value = "/api/bb/trade/ext/createBBAccount")
    void createBBAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation(value = "判断BB账号是否存在")
    @GetMapping(value = "/api/bb/trade/ext/bbAccountExist")
    public Boolean bbAccountExist(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation(value = "获取BB账户")
    @GetMapping(value = "/api/bb/trade/ext/getBBAccount")
    public BbAccountExtVo getBBAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);


    @ApiOperation(value = "获取BB账户(新接口)")
    @GetMapping(value = "/api/bb/trade/ext/getNewBBAccount")
    public BbAccountExtVo getNewBBAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);


    @ApiOperation(value = "获取BB账户余额")
    @GetMapping(value = "/api/bb/trade/ext/getBalance")
    BigDecimal getBalance(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation("根据币种查询平台所有用户的币币账户总额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true)
    })
    @GetMapping(value = "/api/extension/bbAccount/queryTotalNumber")
    public BigDecimal queryTotalNumber(@RequestParam(value = "asset") String asset,@RequestParam(value = "startTime") Long startTime,@RequestParam(value = "endTime") Long endTime);

}