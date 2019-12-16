package com.hp.sh.expv3.pc.extension.service;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcPositionCoreService {
    BigDecimal getPosMargin(Long userId, String asset);
}
