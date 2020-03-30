//package com.hp.sh.expv3.bb.extension.api;
//
//import com.gitee.hupadev.base.api.PageResult;
//import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiImplicitParam;
//import io.swagger.annotations.ApiImplicitParams;
//import io.swagger.annotations.ApiOperation;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.List;
//
///**
// * @author BaiLiJun  on 2020/3/24
// */
//@Api(tags = "账户日志扩展接口")
//@FeignClient(value = "ex-bb-extend")
//public interface BbAccountLogExtApi {
//    @ApiOperation(value = "查询币币账单")
//    @GetMapping(value = "/api/bb/accountLog/ext/query")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "userId", value = "用户id", example = "122036898980999296", required = true),
//            @ApiImplicitParam(name = "asset", value = "资产类型", example = "USDT", required = false),
//            @ApiImplicitParam(name = "historyType", value = "1.最近两天,2.两天到三个月", example = "1", required = true),
//            @ApiImplicitParam(name = "tradeType",value = "类型0.全部,1.买入,2.卖出,3.从资金账户转入,4.转出至资金账户,5.从永续合约转入,6.转出至永续合约" , example = "1", required = true),
//            @ApiImplicitParam(name = "startDate", value = "开始时间", required = false),
//            @ApiImplicitParam(name = "endDate", value = "结束时间", required = false),
//            @ApiImplicitParam(name = "nextPage", value = "翻页标记,-1 上一页,1.下一页", example = "1", required = true),
//            @ApiImplicitParam(name = "lastId", value = "当前页最后一条数据的id", example = "1", required = false),
//            @ApiImplicitParam(name = "pageSize", value = "页行数", example = "20", required = true)
//    })
//    List<BbAccountRecordExtVo> query(@RequestParam(value = "userId", required = true) Long userId,
//                                  @RequestParam(value = "asset", required = false) String asset,
//                                  @RequestParam(value = "historyType", required = true) Integer historyType,
//                                  @RequestParam(value = "tradeType", required = true) Integer tradeType,
//                                  @RequestParam(value = "startDate", required = false) Long startDate,
//                                  @RequestParam(value = "endDate", required = false) Long endDate,
//                                  @RequestParam(value = "nextPage", required = true) Integer nextPage,
//                                  @RequestParam(value = "lastId", required = false) Long lastId,
//                                  @RequestParam(value = "pageSize", required = true, defaultValue = "20") Integer pageSize );
//}
