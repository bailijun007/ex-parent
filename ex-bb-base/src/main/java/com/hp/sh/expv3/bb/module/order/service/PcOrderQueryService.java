package com.hp.sh.expv3.bb.module.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.module.order.dao.PcActiveOrderDAO;
import com.hp.sh.expv3.bb.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.bb.module.order.dao.PcOrderLogDAO;
import com.hp.sh.expv3.bb.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.bb.module.order.entity.PcOrder;
import com.hp.sh.expv3.bb.module.position.entity.PcPosition;
import com.hp.sh.expv3.bb.strategy.PositionStrategyContext;
import com.hp.sh.expv3.bb.strategy.vo.OrderTradeVo;
import com.hp.sh.expv3.bb.vo.response.ActiveOrderVo;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;

@Service
@Transactional(readOnly=true)
public class PcOrderQueryService {

	@Autowired
	private PcOrderDAO pcOrderDAO;

	@Autowired
	private PcActiveOrderDAO pcActiveOrderDAO;

	@Autowired
	private PcOrderLogDAO pcOrderLogDAO;

	@Autowired
	private PcOrderTradeDAO pcOrderTradeDAO;

    @Autowired
    private PositionStrategyContext positionStrategyContext;
    
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
		
		BigDecimal cpv = this.pcOrderDAO.getClosingVolume(pos.getUserId(), pos.getAsset(), pos.getSymbol(), pos.getId());
		if(cpv!=null){
			closablePos = closablePos.subtract(cpv);
		}
		return closablePos;
	}
	
	public boolean hasActiveOrder(long userId, String asset, String symbol, Integer longFlag) {
		long count = this.pcActiveOrderDAO.exist(userId, asset, symbol, longFlag);
		return count>0;
	}
	
	public List<ActiveOrderVo> queryActiveList(long userId, String asset, String symbol){
		List<ActiveOrderVo> result = new ArrayList<ActiveOrderVo>();
		List<PcOrder> list = this.pcOrderDAO.queryActiveOrderList(userId, asset, symbol);
		
		List<Long> _orderIdList = BeanHelper.getDistinctPropertyList(list, "id");
		List<OrderTradeVo> _tradeList = pcOrderTradeDAO.queryOrderTrade(userId, _orderIdList);
		Map<Long, List<OrderTradeVo>> _tradeListMap = BeanHelper.groupByProperty(_tradeList, "orderId");
		
		for(PcOrder order : list){
			ActiveOrderVo activeOrderVo = new ActiveOrderVo();
			activeOrderVo.setId(order.getId());
			activeOrderVo.setUserId(order.getUserId());
			activeOrderVo.setAsset(order.getAsset());
			activeOrderVo.setSymbol(order.getSymbol());
			activeOrderVo.setCreated(order.getCreated());
			activeOrderVo.setCloseFlag(order.getCloseFlag());
			activeOrderVo.setLongFlag(order.getLongFlag());
			activeOrderVo.setLeverage(order.getLeverage());
			activeOrderVo.setFilledVolume(order.getFilledVolume());
			activeOrderVo.setFilledRatio(order.getFilledVolume().divide(order.getVolume(), Precision.COMMON_PRECISION, Precision.LESS));
			
			List<OrderTradeVo> orderTradeList = _tradeListMap.get(order.getId());
			BigDecimal meanPrice = positionStrategyContext.calcOrderMeanPrice(order.getAsset(), order.getSymbol(), order.getLongFlag(), orderTradeList);
			
			activeOrderVo.setMeanPrice(meanPrice);
			activeOrderVo.setPrice(order.getPrice());
			activeOrderVo.setOrderMargin(order.getOrderMargin());
			activeOrderVo.setFeeCost(order.getFeeCost());
			activeOrderVo.setStatus(order.getStatus());
			
			result.add(activeOrderVo);
		}
		return result;
	}
	
	public List<PcOrder> queryRebaseOrder(Page page, Long createdEnd) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activeFlag", IntBool.YES);
		params.put("liqFlag", IntBool.NO);
		params.put("orderBy", "id");
		params.put("asc", true);
		params.put("page", page);
		params.put("status", OrderStatus.PENDING_NEW);
		params.put("createdEnd", createdEnd);
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
