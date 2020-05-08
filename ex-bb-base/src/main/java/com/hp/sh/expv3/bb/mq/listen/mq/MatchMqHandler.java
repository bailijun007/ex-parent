package com.hp.sh.expv3.bb.mq.listen.mq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.lock.LockIt;

@Component
public class MatchMqHandler {
	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private BBTradeService tradeService;
	
	@LockIt(key="U-${userId}")
	public void setCancelled(long userId, String asset, String symbol, long orderId){
		orderService.setCancelled(userId, asset, symbol, orderId);
	}

	@LockIt(key="U-${userId}")
	public void setNewStatus(long userId, String asset, String symbol, long orderId){
		orderService.setNewStatus(userId, asset, symbol, orderId);
	}

	@LockIt(key="U-${userId}")
	public void handleTrade(BBTradeVo msg){
		tradeService.handleTrade(msg);
	}
	
}
