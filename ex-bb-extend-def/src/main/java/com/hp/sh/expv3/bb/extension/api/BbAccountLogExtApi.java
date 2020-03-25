package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/24
 */
@Api(tags = "用户bb资金账户扩展接口")
@FeignClient(value = "ex-bb-extend")
public interface BbAccountLogExtApi {
    @ApiOperation(value = "查询币币账单")
    @GetMapping(value = "/api/bb/accountLog/ext/query")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "tradeType",value = "类型 " , example = "0", required = true),
            @ApiImplicitParam(name = "startDate", value = "开始时间", required = true),
            @ApiImplicitParam(name = "endDate", value = "结束时间", required = true),
            @ApiImplicitParam(name = "nextPage", value = "翻页标记,-1 上一页,1.下一页", example = "1", required = true),
            @ApiImplicitParam(name = "lastOrderId", value = "当前页最后一张委托的id", example = "1", required = false),
            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "20", required = true)
    })
    List<BbAccountLogExtVo> query(@RequestParam(value = "userId", required = true) Long userId,
                                  @RequestParam(value = "asset", required = true) String asset,
                                  @RequestParam(value = "symbol", required = true) String symbol,
                                  @RequestParam(value = "tradeType", required = true) Integer tradeType,
                                  @RequestParam(value = "startDate", required = true) Long startDate,
                                  @RequestParam(value = "endDate", required = true) Long endDate,
                                  @RequestParam(value = "nextPage", required = true) Integer nextPage,
                                  @RequestParam(value = "lastOrderId", required = false) Integer lastOrderId,
                                  @RequestParam(value = "pageSize", required = true, defaultValue = "20") Integer pageSize );
}
