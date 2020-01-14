package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
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


    @ApiOperation(value = "查询仓位列表")
    @GetMapping(value = "/api/extension/pc/position/findPositionList")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "posId", value = "仓位id", example = "1", required = false),
            @ApiImplicitParam(name = "liqStatus", value = "仓位强平状态，0：未触发平仓，1：仓位被冻结", example = "0", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true)
    })
    PageResult<CurrentPositionVo> findPositionList(@RequestParam("userId") Long userId,
                                                   @RequestParam("asset") String asset,
                                                   @RequestParam("posId") Long posId,
                                                   @RequestParam("liqStatus") Integer liqStatus,
                                                   @RequestParam("symbol") String symbol,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam("pageSize") Integer pageSize);



    @ApiOperation(value = "查询已平仓位信息列表")
    @GetMapping(value = "/api/extension/pc/position/selectPosByAccount")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true)
    })
    List<CurrentPositionVo> selectPosByAccount(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                                @RequestParam("symbol") String symbol,@RequestParam("startTime") Long startTime);


}
