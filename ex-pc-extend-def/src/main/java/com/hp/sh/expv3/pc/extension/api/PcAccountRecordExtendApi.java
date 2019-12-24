package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;
import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordExtVo;
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
@Api(tags = "合约账户扩展接口")
@FeignClient(value = "ex-pc-extend")
public interface PcAccountRecordExtendApi {

    @ApiOperation(value = "查询pc永续合约账户")
    @GetMapping(value = "/api/extension/pc/account/record/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "tradeType", value = "类型 0.全部,1.强平平多,2.强平平空,3.转入,4.转出,5.手动追加,6.手动减少,7.自动追加,8.开多,9.开空,10.平多,11.平空,12.调低杠杆追加保证金,13,全部强平,14.全部划转,15.全部交易", example = "1", required = true),
            @ApiImplicitParam(name = "historyType ", value = "1.最近两天,2.两天到三个月", example = "1", required = true),
            @ApiImplicitParam(name = "startDate", value = "开始时间(当history_type是2时,填写)", required = false),
            @ApiImplicitParam(name = "endDate", value = "结束时间 (当history_type是2时,填写)",  required = false),
            @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true),

    })
    PageResult<PcAccountRecordExtVo> findContractAccountList(@RequestParam(value = "userId", required = true) Long userId, @RequestParam(value = "asset", required = true) String asset,
                                                             @RequestParam(value = "tradeType", required = true) Integer tradeType, @RequestParam(value = "historyType", required = true) Integer historyType,
                                                             @RequestParam(value = "startDate", required = false) String startDate, @RequestParam(value = "endDate", required = false) String endDate,
                                                             @RequestParam(value = "pageNo", required = true) Integer pageNo, @RequestParam(value = "pageSize", required = true) Integer pageSize);


}
