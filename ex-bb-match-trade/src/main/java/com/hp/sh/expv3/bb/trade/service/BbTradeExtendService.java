package com.hp.sh.expv3.bb.trade.service;


import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface BbTradeExtendService {
    List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime);

    List<BbTradeVo> selectTradeListByUser(Long userId, String asset, String symbol, Long startTime, Long endTime);

    List<BbTradeVo> queryTradeList(Long userId, String asset, String symbol, Integer count,String startTime,String endTime);

    BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long startTime1);

    List<BbTradeVo> queryTradeResult(String asset, String symbol, Integer count,String startTime,String endTime);

    }
