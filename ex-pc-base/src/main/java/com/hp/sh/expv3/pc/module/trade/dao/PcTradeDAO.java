package com.hp.sh.expv3.pc.module.trade.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.pc.module.trade.entity.PcTrade;

@Mapper
public interface PcTradeDAO extends BaseMapper<PcTrade, Long> {

	public List<PcTrade> queryList(Map<String,Object> params);
	
	public PcTrade queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}