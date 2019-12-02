package com.hp.sh.expv3.fund.cash.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalRecord;
import com.hp.sh.expv3.fund.cash.vo.SumAmount;

/**
 * 
 * @author wangjg
 *
 */
public interface WithdrawalRecordDAO extends BaseAccountDataMapper<WithdrawalRecord,Long> {

	public List<WithdrawalRecord> queryList(Map<String,Object> params);
	
	public WithdrawalRecord findBySn(@Param("userId") Long userId, @Param("sn") String sn);
	
	public List<SumAmount> sumAmount(@Param("userId") Long userId, @Param("asset") String asset, @Param("payStatus") String payStatus);

}
