package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;
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
            @ApiImplicitParam(name = "pageStatus", value = "翻页状态：-1：上一页，1：下一页", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryHistory")
    public List<WithdrawalRecordVo> queryHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset") String asset,
                                                 @RequestParam(value = "queryId", required = false) Long queryId, @RequestParam(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "pageStatus") Integer pageStatus);

    @ApiOperation("获取最新提币历史")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = true),
            @ApiImplicitParam(name = "queryId", value = "充币记录表主键编号", example = "1", required = false),
            @ApiImplicitParam(name = "pageStatus", value = "翻页状态：-1：上一页，1：下一页", example = "1"),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryLastHistory")
    public WithdrawalRecordVo queryLastHistory(@RequestParam(value = "userId") Long userId, @RequestParam(value = "asset") String asset,
                                                 @RequestParam(value = "queryId", required = false) Long queryId, @RequestParam(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "pageStatus") Integer pageStatus);




    @ApiOperation("查询所有用户提币历史 后台admin专用")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "2", required = false),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    @GetMapping(value = "/api/extension/account/withdrawal/queryAllUserHistory")
    public PageResult<WithdrawalRecordVo> queryAllUserHistory(@RequestParam(value = "userId", required = false) Long userId, @RequestParam(value = "asset", required = false) String asset,
                                                              @RequestParam(value = "pageNo", required = true) Integer pageNo, @RequestParam(value = "pageSize") Integer pageSize);


}
