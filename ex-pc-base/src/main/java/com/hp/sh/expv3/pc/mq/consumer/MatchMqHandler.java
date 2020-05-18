package com.hp.sh.expv3.pc.mq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.pc.module.liq.service.PcLiqService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.service.PcTradeService;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcCancelledMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcNotMatchedMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.liq.LiqCancelledMsg;

@Component
public class MatchMqHandler {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqHandler.class);
	
	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private PcTradeService pcTradeService;
	
    @Autowired
    private PcLiqService pcLiqService;

    //撮合未成交
    @LockIt(key="U-${msg.accountId}")
	public void handleNotMatchedMsg(PcNotMatchedMsg msg){
		pcOrderService.setNewStatus(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
	}
	
	//取消订单
    @LockIt(key="U-${msg.accountId}")
	public void handleCancelledMsg(PcCancelledMsg msg){
		pcOrderService.setCancelled(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId(), msg.getCancelNumber());
	}
	
	//成交
    @LockIt(key="U-${msg.accountId}")
	public void handleTradeMsg(PcTradeMsg msg){
		pcTradeService.handleTradeOrder(msg);
	}
	
	//强平取消
    @LockIt(key="U-${msg.accountId}")
	public void handleLiqCancelledMsg(LiqCancelledMsg msg){
		pcLiqService.cancelCloseOrder(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getLongFlag(), msg.getPosId(), msg.getCancelOrders(), msg.getLastFlag(), msg.getLiqMarkPrice());
	}
}
