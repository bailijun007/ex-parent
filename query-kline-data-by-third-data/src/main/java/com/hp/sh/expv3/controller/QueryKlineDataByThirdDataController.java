package com.hp.sh.expv3.controller;

import com.hp.sh.expv3.api.QueryKlineDataByThirdDataControllerApi;
import com.hp.sh.expv3.service.IQueryKlineDataByThirdDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/5/6
 */
@RestController
public class QueryKlineDataByThirdDataController implements QueryKlineDataByThirdDataControllerApi {

    private static final Logger logger = LoggerFactory.getLogger(QueryKlineDataByThirdDataController.class);

    @Autowired
    private IQueryKlineDataByThirdDataService queryKlineDataByThirdDataService;


    @Override
    public void queryKlineDataByThirdData(String tableName, Integer klineType, String asset, String pair, String interval, Long openTimeBegin, Long openTimeEnd) {
        logger.info("查询第三方k线数据，收到参数为:tableName={},klineType={},asset={},pair={},interval={},openTimeBegin={},openTimeEnd={}",tableName,klineType,asset,pair,interval,openTimeBegin,openTimeEnd);
        queryKlineDataByThirdDataService.queryKlineDataByThirdData(tableName, klineType, asset, pair, interval, openTimeBegin, openTimeEnd);
    }

    @Override
    public void queryKlineDataByProd(String tableName, Integer klineType, String asset, String pair, String interval, Long openTimeBegin, Long openTimeEnd) {
        queryKlineDataByThirdDataService.queryKlineDataByProd(tableName, klineType, asset, pair, interval, openTimeBegin, openTimeEnd);

    }

}
