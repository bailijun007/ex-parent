package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordLogVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author BaiLiJun  on 2019/12/25
 */
@Api(tags = "合约账户日志扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcAccountLogExtendApi {

    @ApiOperation(value = "查询pc永续合约账户")
    @GetMapping(value = "/api/extension/pc/account/record/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "163595781968756736", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "ETH", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "ETH_USDT", required = true),
            @ApiImplicitParam(name = "tradeType",
                    value = "类型 " +
                            "0.全部," +
                            "1.开多," +
                            "2.开空," +
                            "3.平多," +
                            "4.平空," +
                            "5.转入," +
                            "6.转出," +
                            "7.手动追加保证金," +
                            "8.手动减少保证金," +
                            "9.自动追加保证金," +
                            "10.调低杠杆追加保证金," +
                            "11.强平平多," +
                            "12.强平平空," +
                            "-1:(转入,转出)," +
                            "-2:(开多,开空,平多,平空)," +
                            "-3:(手动追加保证金,手动减少保证金,自动追加保证金,调低杠杆追加保证金)," +
                            "-4:(强平平多,强平平空),", example = "1", required = true),
            @ApiImplicitParam(name = "historyType ", value = "1.最近两天,2.两天到三个月", example = "1", required = true),
            @ApiImplicitParam(name = "startDate", value = "开始时间(当history_type是2时,填写)", required = false),
            @ApiImplicitParam(name = "endDate", value = "结束时间 (当history_type是2时,填写)", required = false),
            @ApiImplicitParam(name = "queryId", value = "主键id", example = "167958542689533952", required = false),
            @ApiImplicitParam(name = "nextPage", value = "-1:上一页,1:下一页", example = "1", required = true),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = false),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    PageResult<PcAccountRecordLogVo> query(@RequestParam(value = "userId", required = true) Long userId,
                                           @RequestParam(value = "asset", required = true) String asset,
                                           @RequestParam(value = "tradeType", required = true) Integer tradeType,
                                           @RequestParam(value = "historyType", required = true) Integer historyType,
                                           @RequestParam(value = "startDate", required = false) Long startDate,
                                           @RequestParam(value = "endDate", required = false) Long endDate,
                                           @RequestParam(value = "pageNo", required = false) Integer pageNo,
                                           @RequestParam(value = "pageSize", required = true, defaultValue = "20") Integer pageSize,
                                           @RequestParam(value = "symbol", required = true) String symbol,
                                           @RequestParam(value = "queryId", required = false) Long queryId,
                                           @RequestParam(value = "nextPage", required = true) Integer nextPage);


}
