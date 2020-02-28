package com.hp.sh.expv3.bb.extension.strategy.data;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/25
 */
public interface BbOrderTrade {

    BigDecimal getVolume();

    BigDecimal getPrice();
}
