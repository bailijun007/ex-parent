package com.hp.sh.expv3.fund.extension.dao;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/14
 */
public interface WithdrawalRecordExtMapper {


    BigDecimal getFrozenCapital(@Param("userId") Long userId,@Param("asset") String asset);
}
