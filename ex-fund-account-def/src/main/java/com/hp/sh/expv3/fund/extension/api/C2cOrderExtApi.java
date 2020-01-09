package com.hp.sh.expv3.fund.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author BaiLiJun  on 2020/1/9
 */
@Api(tags = "c2c订单Api")
@FeignClient(value = "ex-fund-account")
public interface C2cOrderExtApi {


    @ApiOperation("通过支付状态分页查询c2c订单，不传则查全部")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "主键编号", example = "1", required = false),
            @ApiImplicitParam(name = "payStatus", value = "支付状态:0-待支付，1-支付成功，2-支付失败,3:已取消, 4-同步余额, 5-审核中, 6-审核通过", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "10", required = true),
            @ApiImplicitParam(name = "nextPage", value = "1:下一页，-1：上一页", example = "1", required = true)
    })
    @GetMapping(value = "/api/extension/c2c/order/pageQueryByPayStatus")
    public PageResult<C2cOrderVo> pageQueryByPayStatus(@RequestParam("payStatus") Integer payStatus, @RequestParam("nextPage") Integer nextPage,
                                                       @RequestParam("pageSize") Integer pageSize, @RequestParam(value = "id",required = false) Long id);

}
