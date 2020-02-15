package com.hp.sh.expv3.bb.module.trade.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;

@Mapper
public interface BBMatchedTradeDAO extends BaseMapper<BBMatchedTrade, Long> {

	public List<BBMatchedTrade> queryList(Map<String,Object> params);
	
	public BBMatchedTrade queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public BBMatchedTrade findById(Long id);

	public List<BBMatchedTrade> queryPending(Page page, @Param("userId") Long userId, @Param("startTime") Long startTime, @Param("startId") Long startId);

	public void setMakerHandleStatus(@Param("id") Long id, @Param("makerHandleStatus") Integer makerHandleStatus, @Param("modified") Long modified);
	
	public void setTakerHandleStatus(@Param("id") Long id, @Param("takerHandleStatus") Integer takerHandleStatus, @Param("modified") Long modified);

	public long exist(@Param("mkOrderId") Long mkOrderId, @Param("tkOrderId") Long tkOrderId);

}