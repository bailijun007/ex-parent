package com.hp.sh.expv3.pc.module.order.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.module.order.constant.OrderError;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.mq.MatchMqSender;
import com.hp.sh.expv3.pc.module.order.mq.msg.BookResetMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.module.order.mq.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.utils.BidUtils;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags="订单接口")
@RestController
public class PcOrderApiAction {
	
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
	@ApiOperation(value = "创建订单")
	@GetMapping(value = "/api/pc/order/create")
	public void create(long userId, String cliOrderId, String asset, String symbol, int closeFlag, int longFlag, int timeInForce, BigDecimal price, BigDecimal amt) throws Exception{
		
		//check 检查可平仓位
		//checkShortPosition();
		
		//create
		PcOrder order = pcOrderService.create(userId, cliOrderId, asset, symbol, closeFlag, longFlag, timeInForce, price, amt);

		//send mq
		OrderPendingNewMsg msg = new OrderPendingNewMsg();
		msg.setAccountId(userId);
		msg.setAsset(asset);
		msg.setBidFlag(BidUtils.getBidFlag(closeFlag, longFlag));
		msg.setCloseFlag(closeFlag);
		msg.setDisplayNumber(amt);
		msg.setNumber(amt);
		msg.setOrderId(order.getId());
		msg.setPrice(order.getPrice());
		msg.setSymbol(symbol);
		msg.setOrderType(order.getOrderType());
		msg.setOrderTime(order.getCreated().getTime());
		matchMqSender.sendPendingNew(msg);
	}
	
	@ApiOperation(value = "取消订单")
	@GetMapping(value = "/api/pc/order/cancel")
	public void cancel(long userId, String asset, String symbol, Long orderId) throws Exception{
		PcOrder order = this.pcOrderService.getOrder(userId, orderId);
		if(order.getStatus() == PcOrder.CANCELED){
			throw new ExException(OrderError.CANCELED);
		}
		if(order.getStatus() == PcOrder.FILLED){
			throw new ExException(OrderError.FILLED);
		}
		this.pcOrderService.setCancelStatus(userId, asset, orderId, PcOrder.PENDING_CANCEL);

		//发送消息
		OrderPendingCancelMsg mqMsg = new OrderPendingCancelMsg(userId, asset, symbol, orderId);
		mqMsg.setAccountId(userId);
		mqMsg.setAsset(asset);
		mqMsg.setOrderId(orderId);
		mqMsg.setSymbol(order.getSymbol());
		this.matchMqSender.sendPendingCancel(mqMsg);
		
		//redis 消息
		
	}
	
	@ApiOperation(value = "重置深度")
	@GetMapping(value = "/api/pc/order/pcBookReset")
	public void pcBookReset (String asset, String symbol) throws Exception{
		//发送消息
		BookResetMsg msg = new BookResetMsg(asset, symbol);
		msg.setAsset(asset);
		msg.setSymbol(symbol);
		this.matchMqSender.sendBookResetMsg(msg);
	}

}
