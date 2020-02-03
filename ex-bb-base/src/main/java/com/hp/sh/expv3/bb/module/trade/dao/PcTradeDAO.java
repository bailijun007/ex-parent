package com.hp.sh.expv3.bb.module.trade.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.trade.entity.PcMatchedResult;

@Mapper
public interface PcTradeDAO extends BaseMapper<PcMatchedResult, Long> {

	public List<PcMatchedResult> queryList(Map<String,Object> params);
	
	public PcMatchedResult queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}