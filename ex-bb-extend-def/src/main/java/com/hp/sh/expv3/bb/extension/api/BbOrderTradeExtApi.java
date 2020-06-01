package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeDetailVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbUserOrderTrade;
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

//    @ApiOperation(value = "查小于某个时间点的最大的一条记录")
//    @ApiImplicitParams(
//            {
//                    @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = false),
//                    @ApiImplicitParam(name = "asset", value = "资产类型", example = "USDT", required = true),
//                    @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
//                    @ApiImplicitParam(name = "startTime ", value = "开始时间", required = true),
//                    @ApiImplicitParam(name = "endTime ", value = "结束时间", required = true)
//            }
//    )
//    @GetMapping(value = "/api/bb/trade/ext/selectLessTimeTrade")
//    public BbOrderTradeVo selectLessTimeTrade(@RequestParam(value = "userId",required = false) Long userId,
//                                              @RequestParam(value = "asset") String asset, @RequestParam(value = "symbol") String symbol,
//                                              @RequestParam(value = "statTime",required = true) Long statTime, @RequestParam(value = "endTime",required = true) Long endTime);

//    @ApiOperation(value = "查某个用户的所有成交记录")
//    @GetMapping(value = "/api/bb/trade/ext/selectAllTradeListByUser")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
//            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
//            @ApiImplicitParam(name = "userId ", value = "用户id", required = true)
//    })
//    List<BbOrderTradeVo> selectAllTradeListByUser(
//            @RequestParam(value = "asset", required = false) String asset,
//            @RequestParam(value = "symbol", required = false) String symbol,
//            @RequestParam(value = "userId", required = true) Long userId
//    );

    @ApiOperation(value = "查某个时间区间某个用户的成交记录 (不传时间则默认查今天以前的所有数据)")
    @GetMapping(value = "/api/bb/trade/ext/selectTradeListByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "userId ", value = "用户id", required = true),
            @ApiImplicitParam(name = "id ", value = "id", required = false),
            @ApiImplicitParam(name = "startTime ", value = "开始时间", required = false),
            @ApiImplicitParam(name = "endTime ", value = "结束时间", required = false)
    })
    List<BbUserOrderTrade> selectTradeListByUserId(
            @RequestParam(value = "asset", required = true) String asset,
            @RequestParam(value = "symbol", required = true) String symbol,
            @RequestParam(value = "userId", required = true) Long userId,
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "startTime", required = false) Long startTime,
            @RequestParam(value = "endTime", required = false) Long endTime
    );

    @ApiOperation(value = "查询成交记录列表(后台admin接口)")
    @GetMapping(value = "/api/bb/trade/ext/selectBbFeeCollectByAccountId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "statTime", value = "开始时间", example = "1578886491000", required = true),
            @ApiImplicitParam(name = "endTime", value = "结束时间", example = "1578891531000", required = true)
    })
    List<BbOrderTradeDetailVo> selectBbFeeCollectByAccountId(@RequestParam("asset") String asset, @RequestParam("symbol") String symbol,
                                                             @RequestParam("userId") Long userId, @RequestParam("statTime") Long statTime, @RequestParam("endTime") Long endTime);



    @ApiOperation(value = "获取当前用户交易明细")
    @GetMapping(value = "/api/extension/bb/orderTrade/queryHistory")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产类型", example = "BTC", required = true),
            @ApiImplicitParam(name = "symbol", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "lastTradeId  ", value = "主键id,不传返回最新的20条数据", example = "1", required = false),
            @ApiImplicitParam(name = "nextPage", value = "-1.上一页,1.下一页", example = "1", required = true),
            @ApiImplicitParam(name = "pageSize ", value = "页行数", example = "20", required = true),
            @ApiImplicitParam(name = "startTime", value = "开始时间", example = "2020-05-01", required = false),
            @ApiImplicitParam(name = "endTime", value = "结束时间", example = "2020-05-29", required = false)
    })
    List<BbOrderTradeDetailVo> queryHistory(@RequestParam("userId") Long userId, @RequestParam("asset") String asset,
                                            @RequestParam("symbol") String symbol, @RequestParam(value = "lastTradeId", required = false) Long lastTradeId,
                                            @RequestParam("nextPage") Integer nextPage, @RequestParam("pageSize") Integer pageSize,
                                            @RequestParam(value = "startTime", required = false) Long startTime, @RequestParam(value = "endTime", required = false) Long endTime);

}
