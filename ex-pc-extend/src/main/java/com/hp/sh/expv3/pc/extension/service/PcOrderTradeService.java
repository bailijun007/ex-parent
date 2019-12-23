package com.hp.sh.expv3.pc.extension.service;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcOrderTradeService {
    BigDecimal getRealisedPnlByPosIdAndUserId(Long posId, Long userId);
}
