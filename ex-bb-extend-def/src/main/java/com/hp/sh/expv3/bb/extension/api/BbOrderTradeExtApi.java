package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@Api(tags = "币币订单成交记录接口")
@FeignClient(value = "ex-bb-extend")
public interface BbOrderTradeExtApi {

    @ApiOperation(value = "查小于某个时间点的最大的一条记录")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
                    @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
                    @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
                    @ApiImplicitParam(name = "pageSize", value = "页大小", example = "10", required = true)
            }
    )
    @GetMapping(value = "/api/bb/trade/ext/selectLessTimeTrade")
    public BbOrderTradeVo selectLessTimeTrade(@RequestParam(value = "asset") String asset, @RequestParam(value = "symbol") String symbol,  @RequestParam(value = "statTime")Long statTime);

    @ApiOperation(value = "查某个用户的所有成交记录")
    @GetMapping(value = "/api/bb/trade/ext/selectAllTradeListByUser")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = false),
            @ApiImplicitParam(name = "userId ", value = "用户id", required = true)
    })
    List<BbOrderTradeVo> selectAllTradeListByUser(
            @RequestParam(value = "asset", required = false) String asset,
            @RequestParam(value = "symbol", required = false) String symbol,
            @RequestParam(value = "userId", required = true) Long userId
    );


}
