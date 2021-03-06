package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcOrderTradeExtendService {
    BigDecimal getRealisedPnl(Long posId, Long userId, Long orderId);

    BigDecimal getOrderRealisedPnl(List<PcOrderTradeVo> orderTrades);

    BigDecimal getOrderRealisedPnl(PcOrderVo order, List<PcOrderTradeVo> orderTrades);

    BigDecimal getOrderRealisedPnl(boolean closeFlag, List<PcOrderTradeVo> orderTrades);

    PcOrderTradeVo getPcOrderTrade(Long refId, String asset, String symbol, Long userId, Long time);

    List<PcOrderTradeVo> listPcOrderTrade(List<Long> refIds, String asset, String symbol, Long userId,Long startDate,Long endDate);

    List<PcOrderTradeVo> queryOrderTrade(Long userId, String asset, String symbol, String orderId,Long startTime,Long endTime);

    /**
     * 可主键替代上面的
     * {@link com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService#queryOrderTrade(Long userId, String asset, String symbol, String orderId) }
     *
     * @param userId
     * @param asset
     * @param symbolList
     * @param orderIds
     * @return
     */
    List<PcOrderTradeVo> listOrderTrade(Long userId, String asset, List<String> symbolList, List<Long> orderIds,Long startTime,Long endTime);

    List<PcOrderTradeVo> queryTradeRecords(List<String> assetList, List<String> symbolList, Long gtTradeId, Long ltTradeId, Integer count,Long startTime,Long endTime);

    PcOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime);

    List<PcOrderTradeVo> selectAllTradeListByUser(String asset, String symbol, Long userId);

    List<PcOrderTradeVo> queryLastTradeRecord(String asset, String symbol, Integer count);

    List<PcOrderTradeVo> selectPcFeeCollectByAccountId(String asset, String symbol, Long userId, Long statTime, Long endTime);

    List<PcOrderTradeExtendVo> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId);

    BigDecimal queryPcTradeFee(Long userId, String asset, Integer makerFlag, Long beginTime, Long endTime);

    List<PcOrderTradeDetailVo> queryHistory(Long userId, String asset, String symbol, Long lastTradeId, Integer nextPage, Integer pageSize, Long startTime, Long endTime);
}
