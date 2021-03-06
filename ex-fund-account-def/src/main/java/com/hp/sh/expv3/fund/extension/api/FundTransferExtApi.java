package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Api(tags = "资金划转扩展Api")
@FeignClient(value = "ex-fund-account")
public interface FundTransferExtApi {

    @ApiOperation("查询划转历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "queryId", value = "充币记录表主键编号", example = "1", required = false),
            @ApiImplicitParam(name = "pageStatus", value = "翻页状态：-1：上一页，1：下一页", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/transfer/queryHistory")
    public PageResult<FundTransferExtVo> queryHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                @RequestParam(value = "queryId", required = false) Long queryId, @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize,
                                                @RequestParam(value = "pageStatus") Integer pageStatus);


    @ApiOperation("查询所有用户划转历史记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "status", value = "状态（15：成功，16：失败）", example = "15", required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/transfer/queryAllUserHistory")
    public PageResult<FundTransferExtVo> queryAllUserHistory(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                             @RequestParam(value = "status", required = false) Integer status, @RequestParam(value = "pageSize",defaultValue = "20") Integer pageSize, @RequestParam(value = "pageNo") Integer pageNo);


}
