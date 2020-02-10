package com.hp.sh.expv3.bb.module.trade.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;

@Mapper
public interface BBMatchedTradeDAO extends BaseMapper<BBMatchedTrade, Long> {

	public List<BBMatchedTrade> queryList(Map<String,Object> params);
	
	public BBMatchedTrade queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}