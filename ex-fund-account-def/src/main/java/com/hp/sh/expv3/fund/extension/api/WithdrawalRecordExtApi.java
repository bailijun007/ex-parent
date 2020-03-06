package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordByAdmin;
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
 * 提现记录扩展api
 *
 * @author BaiLiJun  on 2019/12/16
 */
@Api(tags = "提现记录扩展Api")
@FeignClient(value = "ex-fund-account")
public interface WithdrawalRecordExtApi {

    @ApiOperation("获取提币历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = true),
            @ApiImplicitParam(name = "queryId", value = "充币记录表主键编号", example = "1", required = false),
            @ApiImplicitParam(name = "pageStatus", value = "翻页状态：-1：上一页，1：下一页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryHistory")
    public List<WithdrawalRecordVo> queryHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset") String asset,
                                                 @RequestParam(value = "queryId", required = false) Long queryId, @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize,
                                                 @RequestParam(value = "pageStatus") Integer pageStatus);


    @ApiOperation("获取提币历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = true),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryHistoryByAdmin")
    public PageResult<WithdrawalRecordByAdmin> queryHistoryByAdmin(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset") String asset,
                                                                   @RequestParam(value = "pageNo") Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize);


    @ApiOperation("通过时间戳获取用户提币记录（不含提币失败的）")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryHistoryByTime")
    public List<WithdrawalRecordVo> queryHistoryByTime(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset") String asset,
                                                       @RequestParam(value = "startTime") Long startTime, @RequestParam(value = "endTime") Long endTime);


    @ApiOperation("获取最新提币历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryLastHistory")
    public WithdrawalRecordVo queryLastHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset") String asset);


    @ApiOperation("查询某个用户一段时间的提币数量")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = true),
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryUserWithdrawal")
    public List<WithdrawalRecordVo> queryUserWithdrawal(@RequestParam(value = "userId") Long userId,
                                                        @RequestParam(value = "asset", required = false) String asset,
                                                        @RequestParam(value = "startTime") Long startTime,
                                                        @RequestParam(value = "endTime") Long endTime);


    @ApiOperation("查询所有用户提币历史 后台admin专用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = false),
            @ApiImplicitParam(name = "startTime", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(name = "approvalStatus", value = "审批状态(1:审批中 2:审批通过:3:拒绝)", required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryAllUserHistory")
    public PageResult<WithdrawalRecordVo> queryAllUserHistory(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                              @RequestParam(value = "startTime", required = false) Long startTime, @RequestParam(value = "endTime", required = false) Long endTime,
                                                              @RequestParam(value = "approvalStatus", required = false) Integer approvalStatus,
                                                              @RequestParam(value = "pageNo", required = true) Integer pageNo, @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize);


}
