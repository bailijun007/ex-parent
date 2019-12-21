package com.hp.sh.expv3.pc.extension.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcPositionDAO {
    BigDecimal getPosMargin(@Param("userId") Long userId, @Param("asset") String asset);

    BigDecimal getInitMargin(@Param("userId") Long userId, @Param("asset") String asset,@Param("posId")  Long posId);
}
