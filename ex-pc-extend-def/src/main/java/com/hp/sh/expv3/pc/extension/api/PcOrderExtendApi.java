package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.vo.CurrentPositionVo;
import com.hp.sh.expv3.pc.extension.vo.CurrentUserOrderVo;
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
@Api(tags = "永续合约订单（委托）扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcOrderExtendApi {


    @ApiOperation(value = "获取当前用户活动委托")
    @GetMapping(value = "/api/extension/pc/order/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "orderType ", value = "委托类型,1:限价,非必填 ,不填为全部类型"),
            @ApiImplicitParam(name = "longFlag", value = "是否：1-多仓，0-空仓", example = "1"),
            @ApiImplicitParam(name = "closeFlag", value = "是否:1-平仓,0-开", example = "1"),
            @ApiImplicitParam(name = "currentPage ", value = "当前页数", example = "1",required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10",required = true)
    })
    List<CurrentUserOrderVo> findCurrentUserOrder(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                                  @RequestParam("symbol") String symbol, @RequestParam("orderType") Integer orderType,
                                                  @RequestParam("longFlag") Integer longFlag, @RequestParam("closeFlag") Integer closeFlag,
                                                  @RequestParam("currentPage") Integer currentPage, @RequestParam("pageSize") Integer pageSize );


}
