package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.mq.match.MatchMqSender;
import com.hp.sh.expv3.pc.mq.match.msg.BookResetMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.mq.match.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.utils.BidUtils;

@RestController
public class PcOrderApiAction implements PcOrderApi {
	
	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	/**
	 * 创建订单
	 * @param userId 用户ID
	 * @param cliOrderId 客户端订单ID
	 * @param asset 资产
	 * @param symbol 合约品种
	 * @param closeFlag 是否平仓：开/平
	 * @param longFlag 多/空
	 * @param timeInForce 生效机制
	 * @param price 委托价格
	 * @param amt 委托金额
	 * @throws Exception 
	 */
	@Override
	public void create(Long userId, String asset, String symbol, Integer closeFlag, Integer longFlag, Integer timeInForce, BigDecimal price, BigDecimal number, String cliOrderId) throws Exception{
		
		PcOrder order = pcOrderService.create(userId, cliOrderId, asset, symbol, closeFlag, longFlag, timeInForce, price, number);

		//send mq
		OrderPendingNewMsg msg = new OrderPendingNewMsg();
		msg.setAccountId(userId);
		msg.setAsset(asset);
		msg.setBidFlag(BidUtils.getBidFlag(closeFlag, longFlag));
		msg.setCloseFlag(closeFlag);
		msg.setDisplayNumber(number);
		msg.setNumber(number);
		msg.setOrderId(order.getId());
		msg.setPrice(order.getPrice());
		msg.setSymbol(symbol);
		msg.setOrderType(order.getOrderType());
		msg.setOrderTime(order.getCreated().getTime());
		msg.setTimeInForce(order.getTimeInForce());
		matchMqSender.sendPendingNew(msg);
	}
	
	@Override
	public void cancel(Long userId, String asset, String symbol, Long orderId) throws Exception{

		this.pcOrderService.setPendingCancel(userId, asset, symbol, orderId);

		//发送消息
		OrderPendingCancelMsg mqMsg = new OrderPendingCancelMsg(userId, asset, symbol, orderId);
		mqMsg.setAccountId(userId);
		mqMsg.setAsset(asset);
		mqMsg.setOrderId(orderId);
		mqMsg.setSymbol(symbol);
		this.matchMqSender.sendPendingCancel(mqMsg);
		
		//redis 消息
		
	}
	
	@Override
	public void bookReset (String asset, String symbol) throws Exception{
		//发送消息
		BookResetMsg msg = new BookResetMsg(asset, symbol);
		msg.setAsset(asset);
		msg.setSymbol(symbol);
		this.matchMqSender.sendBookResetMsg(msg);
	}

}
