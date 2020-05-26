package com.hp.sh.expv3.bb.module.order.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderDAO;
import com.hp.sh.expv3.bb.module.order.dao.BBOrderTradeDAO;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.strategy.common.BBCommonOrderStrategy;
import com.hp.sh.expv3.bb.vo.response.ActiveOrderVo;
import com.hp.sh.expv3.component.lock.LockConfig;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;

@Service
@Transactional(propagation=Propagation.REQUIRED, rollbackFor=Exception.class)
public class BBOrderQueryService {

	@Autowired
	private BBOrderDAO orderDAO;

	@Autowired
	private BBOrderTradeDAO orderTradeDAO;

	@Autowired
	private BBCommonOrderStrategy orderStrategy;
	
    @Autowired
    private LockConfig lockConfig;

    @Deprecated
	public Long queryCount(Map<String, Object> params) {
		return this.orderDAO.queryCount(params);
	}

    @Deprecated
	public List<BBOrder> queryList(Map<String, Object> params) {
		return this.orderDAO.queryList(params);
	}
	
	public boolean hasActiveOrder(long userId, String asset, String symbol, Integer bidFlag) {
		long count = this.orderDAO.exist(userId, asset, symbol, bidFlag);
		return count>0;
	}
	
	public List<ActiveOrderVo> queryActiveList(long userId, String asset, String symbol){
		List<ActiveOrderVo> result = new ArrayList<ActiveOrderVo>();
		List<BBOrder> list = this.orderDAO.queryActiveOrderList(userId, asset, symbol);
		
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

            activeOrderVo.setMeanPrice(order.getTradeMeanPrice());
            activeOrderVo.setPrice(order.getPrice());
            activeOrderVo.setFeeCost(order.getFeeCost());
            activeOrderVo.setStatus(order.getStatus());

            result.add(activeOrderVo);
		}
		return result;
	}
	

	public List<BBOrder> queryPendingActive(Page page, String asset, String symbol, Long createdEnd, Integer status) {
		List<BBOrder> list = this.orderDAO.queryPendingActiveOrders(page, asset, symbol, createdEnd, status);
		return list;
	}
	
	public BBOrder getOrder(String asset, String symbol, long userId, Long orderId){
		if(lockConfig.usePessimisticLock()){
			BBOrder order = this.orderDAO.lockById(asset, symbol, userId, orderId);
			return order;
		}else{
			BBOrder order = this.orderDAO.findById(asset, symbol, userId, orderId);
			return order;
		}
	}
	
	@CrossDB
	public List<BBOrderTrade> querySynchFee(Page page, String asset, String symbol, Long tradeTimeStart){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", page);
		params.put("feeSynchStatus", IntBool.NO);
		params.put("tradeTimeStart", tradeTimeStart);
		params.put("tradeTimeEnd", DbDateUtils.now());
		params.put("orderBy", "trade_time");
		params.put("asset", asset);
		params.put("symbol", symbol);
		List<BBOrderTrade> list = this.orderTradeDAO.queryList(params);
		return list;
	}
}
