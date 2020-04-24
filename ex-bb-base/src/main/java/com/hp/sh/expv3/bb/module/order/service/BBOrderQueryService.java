package com.hp.sh.expv3.bb.module.order.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.OrderStatus;
import com.hp.sh.expv3.bb.module.order.dao.BBActiveOrderDAO;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderDAO;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderLogDAO;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderTradeDAO;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.strategy.common.BBCommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.vo.OrderTradeVo;
import com.hp.sh.expv3.bb.vo.response.ActiveOrderVo;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
public class BBOrderQueryService {

	@Autowired
	private BBOrderDAO bbOrderDAO;

	@Autowired
	private BBActiveOrderDAO bBActiveOrderDAO;

	@Autowired
	private BBOrderLogDAO bBOrderLogDAO;

	@Autowired
	private BBOrderTradeDAO bBOrderTradeDAO;

	@Autowired
	private BBCommonOrderStrategy orderStrategy;

	public Long queryCount(Map<String, Object> params) {
		return this.bbOrderDAO.queryCount(params);
	}

	public List<BBOrder> queryList(Map<String, Object> params) {
		return this.bbOrderDAO.queryList(params);
	}
	
	public boolean hasActiveOrder(long userId, String asset, String symbol, Integer bidFlag) {
		long count = this.bBActiveOrderDAO.exist(userId, asset, symbol, bidFlag);
		return count>0;
	}
	
	public List<ActiveOrderVo> queryActiveList(long userId, String asset, String symbol){
		List<ActiveOrderVo> result = new ArrayList<ActiveOrderVo>();
		List<BBOrder> list = this.bbOrderDAO.queryActiveOrderList(userId, asset, symbol);
		
		List<Long> _orderIdList = BeanHelper.getDistinctPropertyList(list, "id");
		List<OrderTradeVo> _tradeList = bBOrderTradeDAO.queryOrderTrade(userId, _orderIdList);
		Map<Long, List<OrderTradeVo>> _tradeListMap = BeanHelper.groupByProperty(_tradeList, "orderId");
		
		for(BBOrder order : list){
			ActiveOrderVo activeOrderVo = new ActiveOrderVo();
            activeOrderVo.setId(order.getId());
            activeOrderVo.setUserId(order.getUserId());
            activeOrderVo.setAsset(order.getAsset());
            activeOrderVo.setSymbol(order.getSymbol());
            activeOrderVo.setCreated(order.getCreated());
            activeOrderVo.setBidFlag(order.getBidFlag());
            activeOrderVo.setLeverage(order.getLeverage());
            activeOrderVo.setFilledVolume(order.getFilledVolume());
            activeOrderVo.setFilledRatio(order.getFilledVolume().divide(order.getVolume(), Precision.COMMON_PRECISION, Precision.LESS));

            List<OrderTradeVo> orderTradeList = _tradeListMap.get(order.getId());
            BigDecimal meanPrice = orderStrategy.calcOrderMeanPrice(order.getAsset(), order.getSymbol(), orderTradeList);

            activeOrderVo.setMeanPrice(meanPrice);
            activeOrderVo.setPrice(order.getPrice());
            activeOrderVo.setFeeCost(order.getFeeCost());
            activeOrderVo.setStatus(order.getStatus());

            result.add(activeOrderVo);
		}
		return result;
	}
	

	public List<BBOrder> queryPendingActive(Page page, String symbol, Long createdEnd, Integer status) {
		List<BBOrder> list = this.bbOrderDAO.queryPendingActiveOrders(page, symbol, createdEnd, status, IntBool.NO);
		return list;
	}
	
	public List<BBOrder> queryRebaseOrder(Page page, Long createdEnd) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("activeFlag", IntBool.YES);
		params.put("liqFlag", IntBool.NO);
		params.put("orderBy", "id");
		params.put("asc", true);
		params.put("page", page);
		params.put("status", OrderStatus.PENDING_NEW);
		params.put("createdEnd", createdEnd);
		List<BBOrder> list = this.bbOrderDAO.queryList(params);
		return list;
	}
	
	public BBOrder getOrder(long userId, Long orderId){
		BBOrder order = this.bbOrderDAO.findById(userId, orderId);
		return order;
	}
	
	@CrossDB
	public List<BBOrder> pageQuery(Page page, Integer status, Long modified){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("status", status);
		params.put("modifiedEnd", modified);
		List<BBOrder> list = this.bbOrderDAO.queryList(params);
		return list;
	}
	
	@CrossDB
	public List<BBOrderTrade> querySynchFee(Page page, Long tradeTimeStart){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("feeSynchStatus", IntBool.NO);
		params.put("tradeTimeStart", tradeTimeStart);
		params.put("orderBy", "trade_time");
		List<BBOrderTrade> list = this.bBOrderTradeDAO.queryList(params);
		return list;
	}
}
