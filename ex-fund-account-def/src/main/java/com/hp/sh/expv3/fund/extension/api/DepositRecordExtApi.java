package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Api(tags = "充值记录扩展Api")
@FeignClient(value = "ex-fund-account")
public interface DepositRecordExtApi {

    @ApiOperation("查询充币历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC"),
            @ApiImplicitParam(name = "queryId", value = "充币记录表主键编号", example = "1",required = false),
            @ApiImplicitParam(name = "pageStatus", value = "翻页状态：-1：上一页，1：下一页", example = "1",required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/deposit/queryHistory")
    public List<DepositRecordHistoryVo> queryHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                     @RequestParam(value = "queryId", required = false) Long queryId, @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                                                     @RequestParam(value = "pageStatus") Integer pageStatus);



    @ApiOperation("查询所有用户充币历史记录 后台admin专用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/deposit/queryAllUserHistory")
    public PageResult<DepositRecordHistoryVo> queryAllUserHistory(@RequestParam(value = "userId",required = false) Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                                  @RequestParam(value = "pageNo") Integer pageNo, @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize);


    @ApiOperation("根据币种和支付状态查询平台所有用户的充币总数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "payStatus", value = "执行状态:0-提现中，1-提现成功，2-提现失败", example = "1", required = true)
    })
    @GetMapping(value = "/api/extension/depositRecord/queryTotalNumber")
    public BigDecimal queryTotalNumber(@RequestParam(value = "asset") String asset,
                                       @RequestParam(value = "payStatus") Integer payStatus);


}
