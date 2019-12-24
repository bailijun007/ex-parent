package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcOrderTradeService {
    BigDecimal getRealisedPnl(Long posId, Long userId);

    List<PcOrderTradeVo> queryOrderTrade(Long userId, String asset, String symbol, String orderId);
}
