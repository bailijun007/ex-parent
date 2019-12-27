package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.vo.PcAccountSettingVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@Api(tags = "合约账户设置扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcAccountSymbolExtendApi {

    @ApiOperation(value = "查询pc永续合约的用户配置")
    @GetMapping(value = "/api/extension/pc/setting/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true)
    })
    PcAccountSettingVo query(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                             @RequestParam("symbol") String symbol);


}
