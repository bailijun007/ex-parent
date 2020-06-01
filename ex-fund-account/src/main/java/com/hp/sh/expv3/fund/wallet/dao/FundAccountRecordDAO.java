
package com.hp.sh.expv3.fund.wallet.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseAccountDataMapper;
import com.hp.sh.expv3.fund.wallet.entity.FundAccountRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface FundAccountRecordDAO extends BaseAccountDataMapper<FundAccountRecord,Long> {

	public List<FundAccountRecord> queryList(Map<String,Object> params);
	
	public FundAccountRecord findByTradeNo(@Param("userId") Long userId, @Param("tradeNo") String tradeNo);

}
