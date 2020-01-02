package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcOrderTradeExtendService {
    BigDecimal getRealisedPnl(Long posId, Long userId);

    PcOrderTradeVo getPcOrderTrade(Long refId, String asset, String symbol, Long userId, Long time);


    List<PcOrderTradeVo> queryTradeRecords(List<String> assetList, List<String> symbolList, Long gtTradeId, Long ltTradeId, Integer count);

    PcOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime);

    List<PcOrderTradeVo> selectAllTradeListByUser(String asset, String symbol, Long userId);

    List<PcOrderTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long statTime, Long endTime,Long userId);

    List<PcOrderTradeVo> queryLastTradeRecord(String asset, String symbol, Integer count);

}
