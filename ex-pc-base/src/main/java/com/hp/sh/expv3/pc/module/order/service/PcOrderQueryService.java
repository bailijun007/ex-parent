package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderLogDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.utils.IntBool;

@Service
@Transactional(readOnly=true)
public class PcOrderQueryService {

	@Autowired
	private PcOrderDAO pcOrderDAO;

	@Autowired
	private PcOrderLogDAO pcOrderLogDAO;

	public Long queryCount(Map<String, Object> params) {
		return this.pcOrderDAO.queryCount(params);
	}

	public List<PcOrder> queryList(Map<String, Object> params) {
		return this.pcOrderDAO.queryList(params);
	}
	
	/*
	 * 获取可平仓位
	 */
	public BigDecimal getClosingVolume(PcPosition pos) {
		BigDecimal closablePos = pos.getVolume();
		
		BigDecimal cpv = this.pcOrderDAO.getClosingVolume(pos.getUserId(), pos.getId());
		if(cpv!=null){
			closablePos = closablePos.subtract(cpv);
		}
		return closablePos;
	}
	
	public boolean hasActiveOrder(long userId, String asset, String symbol, Integer longFlag) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		params.put("longFlag", longFlag);
		params.put("activeFlag", IntBool.YES);
		params.put("liqFlag", IntBool.NO);
		Long count = this.pcOrderDAO.queryCount(params);
		return count>0;
	}
	
	public List<PcOrder> queryActiveOrder(Long userId, String asset, String symbol, Integer longFlag) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("asset", asset);
		params.put("symbol", symbol);
		params.put("longFlag", longFlag);
		params.put("activeFlag", IntBool.YES);
		params.put("liqFlag", IntBool.NO);
		List<PcOrder> list = this.pcOrderDAO.queryList(params);
		return list;
	}
	
	public PcOrder getOrder(long userId, Long orderId){
		PcOrder order = this.pcOrderDAO.findById(userId, orderId);
		return order;
	}
	
	@CrossDB
	public List<PcOrder> pageQuery(Page page, Integer status, Long modified){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("status", status);
		params.put("modifiedEnd", modified);
		List<PcOrder> list = this.pcOrderDAO.queryList(params);
		return list;
	}
}
