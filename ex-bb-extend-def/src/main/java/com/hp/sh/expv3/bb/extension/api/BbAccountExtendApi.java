/**
 *
 */
package com.hp.sh.expv3.bb.extension.api;

import java.math.BigDecimal;

import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;
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
    @GetMapping(value = "/api/bbAccount/ext/createBBAccount")
    void createBBAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation(value = "判断BB账号是否存在")
    @GetMapping(value = "/api/bbAccount/ext/bbAccountExist")
    public Boolean bbAccountExist(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation(value = "获取BB账户")
    @GetMapping(value = "/api/bbAccount/ext/getBBAccount")
    public BbAccountVo getBBAccount(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);

    @ApiOperation(value = "获取BB账户余额")
    @GetMapping(value = "/api/bbAccount/ext/getBalance")
    BigDecimal getBalance(@RequestParam(value = "userId") Long userId, @RequestParam("asset") String asset);


}