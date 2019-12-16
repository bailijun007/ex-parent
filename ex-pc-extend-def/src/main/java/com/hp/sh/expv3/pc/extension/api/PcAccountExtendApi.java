/**
 *
 */
package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.util.List;

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
@Api(tags = "合约账户扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcAccountExtendApi {

    @ApiOperation(value = "获取账户余额")
    @GetMapping(value = "/api/pc_extend/pc_account/balance")
    BigDecimal getBalance(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation(value = "获取合约账户")
    @GetMapping(value = "/baseUrl/account/pc/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userIds", value = "多个用户id，以逗号分割", example = "1,2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true)
    })
    List<PcAccountExtVo> findContractAccount(@RequestParam("userIds") String userIds, @RequestParam("asset") String asset);


}