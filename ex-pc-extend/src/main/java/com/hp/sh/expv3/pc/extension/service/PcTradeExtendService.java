package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcTradeExtendService {
    List<PcTradeVo> queryTradeResult(String asset, String symbol, Integer count);

    List<PcTradeVo> queryTradeByGtTime(String asset, String symbol, Long startTime, Long endTime, Integer type);


    PcTradeVo queryLastTrade(String asset, String symbol, Long startTime);

    List<PcTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime,Long userId);

    List<PcTradeVo> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId);
}
