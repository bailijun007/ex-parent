package com.hp.sh.expv3.pc.module.account.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecordTradeNo;

/**
 * 
 * @author wangjg
 *
 */
public interface PcAccountRecordTradeNoDAO extends BaseMapper<PcAccountRecordTradeNo,String> {

	public List<PcAccountRecordTradeNo> queryList(Map<String,Object> params);
	
	public PcAccountRecordTradeNo queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);
	
	public PcAccountRecordTradeNo findByTradeNo(String tradeNo);

}
