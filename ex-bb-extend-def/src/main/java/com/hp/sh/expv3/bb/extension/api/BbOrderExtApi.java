package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.BbHistoryOrderVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@Api(tags = "币币委托订单接口")
@FeignClient(value = "ex-bb-extend")
public interface BbOrderExtApi {



    @ApiOperation(value = "查询所有用户币币账号交易记录")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
                    @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = false),
                    @ApiImplicitParam(name = "pageNo", value = "当前页", example = "1", required = true),
                    @ApiImplicitParam(name = "pageSize", value = "页大小", example = "10", required = true)
            }
    )
    @GetMapping(value = "/api/bb/trade/ext/queryAllBbOrederHistory")
    PageResult<BbOrderVo> queryAllBbOrederHistory(@RequestParam(value = "userId",required = false) Long userId, @RequestParam(value = "asset",required = false) String asset,
                                                    @RequestParam(value = "pageSize")  Integer pageSize, @RequestParam(value = "pageNo") Integer pageNo);


    @ApiOperation(value = "查询历史委托")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
                    @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
                    @ApiImplicitParam(name = "symbol", value = "交易对", required = false),
                    @ApiImplicitParam(name = "bidFlag", value = " 1.买入,0.卖出,非必填", required = false),
                    @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true),
                    @ApiImplicitParam(name = "lastOrderId ", value = "当前页最后一张委托的id", example = "10", required = false),
                    @ApiImplicitParam(name = "nextPage ", value = " 翻页标记,-1 上一页,1.下一页 ", example = "1", required = true)
            }
    )
    @GetMapping(value = "/api/bb/order/ext/queryHistoryOrderList")
    List<BbHistoryOrderVo> queryHistoryOrderList(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam(value = "symbol",required = false) String symbol,
                                                 @RequestParam(value = "bidFlag",required = false) Integer bidFlag,
                                                 @RequestParam(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "lastOrderId",required = false) Long lastOrderId,
                                                 @RequestParam(value = "nextPage") Integer nextPage);

    @ApiOperation(value = "查询活动委托")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
                    @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
                    @ApiImplicitParam(name = "symbol", value = "交易对", required = false),
                    @ApiImplicitParam(name = "bidFlag", value = " 1.买入,0.卖出,非必填", required = false),
                    @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "10", required = true),
                    @ApiImplicitParam(name = "lastOrderId ", value = "当前页最后一张委托的id", example = "10", required = false),
                    @ApiImplicitParam(name = "nextPage ", value = " 翻页标记,-1 上一页,1.下一页 ", example = "1", required = true)
            }
    )
    @GetMapping(value = "/api/bb/order/ext/queryBbActiveOrderList")
    List<BbHistoryOrderVo> queryBbActiveOrderList(@RequestParam("userId") Long userId, @RequestParam("asset") String asset, @RequestParam(value = "symbol",required = false) String symbol,
                                                 @RequestParam(value = "bidFlag",required = false) Integer bidFlag,
                                                 @RequestParam(value = "pageSize") Integer pageSize,
                                                 @RequestParam(value = "lastOrderId",required = false) Long lastOrderId,
                                                 @RequestParam(value = "nextPage") Integer nextPage);

}
