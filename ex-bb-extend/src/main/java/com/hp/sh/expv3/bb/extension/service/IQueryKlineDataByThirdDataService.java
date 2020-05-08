package com.hp.sh.expv3.bb.extension.service;

/**
 * @author BaiLiJun  on 2020/5/6
 */
public interface IQueryKlineDataByThirdDataService {
    void queryKlineDataByThirdData(String tableName, Integer klineType, String asset, String pair, String interval, Long openTimeBegin, Long openTimeEnd);
}
