/**
 *
 */
package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.util.List;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author wangjg
 */
@Api(tags = "用户资金账户扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcAccountExtendApi {

    @ApiOperation(value = "获取账户余额")
    @GetMapping(value = "/api/pc_extend/pc_account/balance")
    BigDecimal getBalance(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation(value = "获取合约账户")
    @GetMapping(value = "/api/extension/account/pc/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds", value = "多个用户id，以逗号分割", example = "1,2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true)
    })
    List<PcAccountExtVo> findContractAccount(@RequestParam("userIds") String userIds, @RequestParam("asset") String asset);


    @ApiOperation(value = "获取合约账单列表")
    @GetMapping(value = "/api/extension/account/pc/findContractAccountList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true),

    })
    PageResult<PcAccountExtVo> findContractAccountList(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                       @RequestParam(value = "pageNo", required = true) Integer pageNo, @RequestParam(value = "pageSize", required = true,defaultValue = "20") Integer pageSize);


    @ApiOperation("根据币种查询平台所有用户的合约账户总额")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true)
    })
    @GetMapping(value = "/api/extension/pcAccount/queryTotalNumber")
    public BigDecimal queryTotalNumber(@RequestParam(value = "asset") String asset);

}