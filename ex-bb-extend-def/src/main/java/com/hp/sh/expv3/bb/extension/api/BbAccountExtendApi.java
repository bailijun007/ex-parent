/**
 *
 */
package com.hp.sh.expv3.bb.extension.api;

import java.math.BigDecimal;

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
    @GetMapping(value = "/api/bb/account/createBBAccount")
    int createBBAccount(@RequestParam("userId") Long userId, @RequestParam("asset") String asset);


}