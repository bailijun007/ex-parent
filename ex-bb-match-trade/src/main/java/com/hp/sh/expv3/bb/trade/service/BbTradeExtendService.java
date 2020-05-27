package com.hp.sh.expv3.bb.trade.service;


import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface BbTradeExtendService {
    List<BbTradeVo> queryTradeResult(String asset, String symbol, Integer count);

    List<BbTradeVo> queryTradeByGtTime(String asset, String symbol, Long startTime, Long endTime, Integer type);


    BbTradeVo queryLastTrade(String asset, String symbol, Long startTime);

    List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime, Long userId);

//    List<BbTradeVo> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId);
}
