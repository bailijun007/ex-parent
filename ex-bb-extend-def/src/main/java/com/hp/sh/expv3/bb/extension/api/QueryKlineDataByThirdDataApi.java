package com.hp.sh.expv3.bb.extension.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author BaiLiJun  on 2020/5/8
 */
@Api(tags = "第三方k线数据")
@FeignClient(value = "ex-bb-extend")
public interface QueryKlineDataByThirdDataApi {

    @ApiOperation(value = "通过第三方k线数据修复本平台k线数据)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tableName ", value = "表名称", example = "kline_data_202005", required = true),
            @ApiImplicitParam(name = "klineType", value = "1.现货k线,2.合约k线", example = "1", required = true),
            @ApiImplicitParam(name = "asset", value = "资产", example = "USDT", required = true),
            @ApiImplicitParam(name = "pair", value = "交易对", example = "BTC_USDT", required = true),
            @ApiImplicitParam(name = "interval", value = "单位时间 m -> 分钟; h -> 小时; d -> 天; w -> 周; M -> 月,例 1分钟1m", example = "1min", required = true),
            @ApiImplicitParam(name = "openTimeBegin ", value = "开始时间", example = "1588833240000", required = true),
            @ApiImplicitParam(name = "openTimeEnd ", value = "结束时间", example = "1588844040000", required = true),
    })
    @GetMapping(value = "/api/klineData/query")
    public void queryKlineDataByThirdData(@RequestParam("tableName") String tableName, @RequestParam("klineType")Integer klineType,
                                          @RequestParam(name = "asset") String asset, @RequestParam("pair") String pair,
                                          @RequestParam("interval") String interval, @RequestParam("openTimeBegin") Long openTimeBegin,
                                          @RequestParam("openTimeEnd") Long openTimeEnd) ;


    }
