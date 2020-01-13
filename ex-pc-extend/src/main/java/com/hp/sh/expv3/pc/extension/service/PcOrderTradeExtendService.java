package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcOrderTradeExtendService {
    BigDecimal getRealisedPnl(Long posId, Long userId, Long orderId);

    PcOrderTradeVo getPcOrderTrade(Long refId, String asset, String symbol, Long userId, Long time);

    List<PcOrderTradeVo> queryOrderTrade(Long userId, String asset, String symbol, String orderId);

    /**
     * 可主键替代上面的
     * {@link com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService#queryOrderTrade(Long userId, String asset, String symbol, String orderId) }
     *
     * @param userId
     * @param asset
     * @param symbol
     * @param orderIds
     * @return
     */
    List<PcOrderTradeVo> listOrderTrade(Long userId, String asset, String symbol, List<Long> orderIds);

    List<PcOrderTradeVo> queryTradeRecords(List<String> assetList, List<String> symbolList, Long gtTradeId, Long ltTradeId, Integer count);

    PcOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime);

    List<PcOrderTradeVo> selectAllTradeListByUser(String asset, String symbol, Long userId);

    List<PcOrderTradeVo> queryLastTradeRecord(String asset, String symbol, Integer count);

}
