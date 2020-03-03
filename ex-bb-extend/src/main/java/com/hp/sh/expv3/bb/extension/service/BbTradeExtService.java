package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbTradeExtService {
    List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime);

    List<BbTradeVo> selectTradeListByUser(Long userId, String asset, String symbol, Long startTime, Long endTime);

    List<BbTradeVo> queryTradeList(Long userId, String asset, String symbol, Integer count);

    BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long startTime1);

    List<BbTradeVo> queryLastTrade(String asset, String symbol, Integer count);

    List<BbTradeVo> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId);
}
