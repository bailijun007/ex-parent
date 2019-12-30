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
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USD", required = true),
            @ApiImplicitParam(name = "tradeType", value = "类型 0.全部,1.成交开多,2.成交开空,3.成交平多,4.成交平空,5.转入,6.转出,7.手动追加保证金,8.减少保证金,9.自动追加保证金,10.调低杠杆追加保证金,11.强平平多,12强平平空,", example = "1", required = true),
            @ApiImplicitParam(name = "historyType ", value = "1.最近两天,2.两天到三个月", example = "1", required = true),
            @ApiImplicitParam(name = "startDate", value = "开始时间(当history_type是2时,填写)", required = false),
            @ApiImplicitParam(name = "endDate", value = "结束时间 (当history_type是2时,填写)",  required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true)
    })
    PageResult<PcAccountRecordLogVo> findContractAccountList(@RequestParam(value = "userId", required = true) Long userId, @RequestParam(value = "asset", required = true) String asset,
                                                             @RequestParam(value = "tradeType", required = true) Integer tradeType, @RequestParam(value = "historyType", required = true) Integer historyType,
                                                             @RequestParam(value = "startDate", required = false) Long startDate, @RequestParam(value = "endDate", required = false) Long endDate,
                                                             @RequestParam(value = "pageNo", required = true) Integer pageNo, @RequestParam(value = "pageSize", required = true) Integer pageSize,
                                                             @RequestParam(value = "symbol", required = true) String symbol);


}
