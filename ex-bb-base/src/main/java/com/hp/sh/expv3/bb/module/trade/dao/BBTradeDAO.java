package com.hp.sh.expv3.bb.module.trade.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedResult;

@Mapper
public interface BBTradeDAO extends BaseMapper<BBMatchedResult, Long> {

	public List<BBMatchedResult> queryList(Map<String,Object> params);
	
	public BBMatchedResult queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}