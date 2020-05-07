package com.hp.sh.expv3.bb.module.account.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecordTradeNo;

/**
 * 
 * @author wangjg
 *
 */
public interface BBAccountRecordTradeNoDAO extends BaseMapper<BBAccountRecordTradeNo,String> {

	public List<BBAccountRecordTradeNo> queryList(Map<String,Object> params);
	
	public BBAccountRecordTradeNo queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);
	
	public BBAccountRecordTradeNo findByTradeNo(String tradeNo);

}
