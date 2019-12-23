package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.vo.CurrentPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Api(tags = "永续合约仓位扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcPositionExtendApi {


    @ApiOperation(value = "查询当前活动仓位")
    @GetMapping(value = "/api/extension/pc/position/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol ", value = "交易对", required = true)
    })
    List<CurrentPositionVo> findCurrentPosition(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                                @RequestParam("symbol") String symbol);


}
