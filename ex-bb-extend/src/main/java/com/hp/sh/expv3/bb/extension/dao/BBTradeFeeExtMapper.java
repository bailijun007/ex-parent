package com.hp.sh.expv3.bb.extension.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Param;

/**
 * @author BaiLiJun on 2020/2/15
 */
public interface BBTradeFeeExtMapper {

	BigDecimal query(
			@Param("userId") Long userId, 
			@Param("asset") String asset, 
			@Param("makerFlag") Integer makerFlag,
			@Param("beginTime") Long beginTime, 
			@Param("endTime") Long endTime);

}
