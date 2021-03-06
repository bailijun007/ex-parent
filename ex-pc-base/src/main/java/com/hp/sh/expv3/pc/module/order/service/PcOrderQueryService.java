package com.hp.sh.expv3.pc.module.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.component.lock.LockConfig;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.constant.OrderStatus;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.strategy.vo.OrderTradeVo;
import com.hp.sh.expv3.pc.vo.response.ActiveOrderVo;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;

@Service
@Transactional(readOnly=true)
public class PcOrderQueryService {

	@Autowired
	private PcOrderDAO pcOrderDAO;

	@Autowired
	private PcOrderTradeDAO pcOrderTradeDAO;

    @Autowired
    private PcStrategyContext pcStrategyContext;
    
    @Autowired
    private LockConfig lockConfig;
    
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
		long count = this.pcOrderDAO.exist(userId, asset, symbol, longFlag);
		return count>0;
	}
	
	public List<ActiveOrderVo> queryUserActiveList(Page page, long userId, String asset, String symbol, Long startId){
		List<ActiveOrderVo> result = new ArrayList<ActiveOrderVo>();
		List<PcOrder> list = this.pcOrderDAO.queryUserActiveOrderList(page, userId, asset, symbol, null, IntBool.NO, startId);
		
		if(list==null||list.isEmpty()){
			return Collections.emptyList();
		}
		
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
			activeOrderVo.setVolume(order.getVolume());
			activeOrderVo.setFilledVolume(order.getFilledVolume());
			activeOrderVo.setFilledRatio(order.getFilledVolume().divide(order.getVolume(), Precision.COMMON_PRECISION, Precision.LESS));
			
			List<OrderTradeVo> orderTradeList = _tradeListMap.get(order.getId());
			BigDecimal meanPrice = pcStrategyContext.calcOrderMeanPrice(order.getAsset(), order.getSymbol(), order.getLongFlag(), orderTradeList);
			
			activeOrderVo.setMeanPrice(meanPrice);
			activeOrderVo.setPrice(order.getPrice());
			activeOrderVo.setOrderMargin(order.getOrderMargin());
			activeOrderVo.setFeeCost(order.getFeeCost());
			activeOrderVo.setStatus(order.getStatus());
			
			activeOrderVo.setOrderType(order.getOrderType());
			activeOrderVo.setClientOrderId(order.getClientOrderId());
			
			result.add(activeOrderVo);
		}
		return result;
	}

	public List<PcOrder> queryPendingActive(Page page, String asset, String symbol, Long createdEnd, Integer status, Long startId) {
		List<PcOrder> list = this.pcOrderDAO.queryActiveOrderList(page, asset, symbol, createdEnd, status, IntBool.NO, startId);
		return list;
	}

	public List<PcOrder> queryLiqCutOrders(Page page, Long createdEnd, Long startId) {
		List<PcOrder> list = this.pcOrderDAO.queryActiveOrderList(page, null, null, createdEnd, OrderStatus.CANCELED, IntBool.YES, startId);
		return list;
	}
	
	public PcOrder getOrder(long userId, Long orderId){
		if(lockConfig.usePessimisticLock()){
			PcOrder order = this.pcOrderDAO.lockById(userId, orderId);
			return order;
		}else{
			PcOrder order = this.pcOrderDAO.findById(userId, orderId);
			return order;
		}
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

	public List<PcOrderTrade> querySynchFee(Page page, String asset, String symbol, Long startTime) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("asset", asset);
		params.put("symbol", symbol);
		params.put("feeSynchStatus", IntBool.NO);
		params.put("tradeTimeStart", startTime);
		params.put("tradeTimeEnd", DbDateUtils.now());
		params.put("orderBy", "trade_time");
		List<PcOrderTrade> list = this.pcOrderTradeDAO.queryList(params);
		return list;
	}
}
