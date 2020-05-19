package com.hp.sh.expv3.api;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author BaiLiJun  on 2020/5/15
 */
@FeignClient(value = "query-kline-data-by-third-data")
public interface QueryKlineDataByThirdDataControllerApi {

    @GetMapping(value = "/api/klineData/query")
    public void queryKlineDataByThirdData(@RequestParam("tableName") String tableName, @RequestParam("klineType")Integer klineType, @RequestParam(name = "asset") String asset, @RequestParam("pair") String pair, @RequestParam("interval") String interval, @RequestParam("openTimeBegin") Long openTimeBegin, @RequestParam("openTimeEnd") Long openTimeEnd);


    @GetMapping(value = "/api/klineData/queryByProd")
    public void queryKlineDataByProd(@RequestParam("tableName") String tableName, @RequestParam("klineType")Integer klineType, @RequestParam(name = "asset") String asset, @RequestParam("pair") String pair, @RequestParam("interval") String interval, @RequestParam("openTimeBegin") Long openTimeBegin, @RequestParam("openTimeEnd") Long openTimeEnd);


}
