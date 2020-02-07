package com.hp.sh.expv3.bb.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.bb.module.account.service.BBAccountCoreService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.service.BBOrderQueryService;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.mq.MatchMqSender;
import com.hp.sh.expv3.bb.mq.match.msg.BookResetMsg;
import com.hp.sh.expv3.bb.mq.match.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.bb.mq.match.msg.OrderPendingNewMsg;
import com.hp.sh.expv3.bb.vo.response.ActiveOrderVo;

@RestController
public class BBOrderApiAction implements BBOrderApi {

	@Autowired
	private BBAccountCoreService bBAccountCoreService;
	
	@Autowired
	private BBOrderService bBOrderService;

	@Autowired
	private BBOrderQueryService orderQueryService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	/**
	 * 创建订单
	 * @param userId 用户ID
	 * @param cliOrderId 客户端订单ID
	 * @param asset 资产
	 * @param symbol 币币交易品种
	 * @param bidFlag 是否平仓：开/平
	 * @param longFlag 多/空
	 * @param timeInForce 生效机制
	 * @param price 委托价格
	 * @param amt 委托金额
	 * @throws Exception 
	 */
	@Override
	public Long create(Long userId, String asset, String symbol, Integer bidFlag, Integer timeInForce, BigDecimal price, BigDecimal number, String cliOrderId){
		
		BBOrder order = bBOrderService.create(userId, cliOrderId, asset, symbol, bidFlag, timeInForce, price, number);

		//send mq
		this.sendOrderMsg(order);
		
		return order.getId();
	}
	
	@Override
	public void cancel(Long userId, String asset, String symbol, Long orderId) {

		this.bBOrderService.setPendingCancel(userId, asset, symbol, orderId);

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
	public void setCancelled(Long userId, String asset, String symbol, Long orderId) {

		this.bBOrderService.setCancelled(userId, asset, symbol, orderId);
		
	}
	
	@Override
	public void bookReset (String asset, String symbol){
		//发送消息
		BookResetMsg msg = new BookResetMsg(asset, symbol);
		msg.setAsset(asset);
		msg.setSymbol(symbol);
		this.matchMqSender.sendBookResetMsg(msg);
	}

	@Override
	public List<ActiveOrderVo> queryActiveList(Long userId, String asset, String symbol){
		List<ActiveOrderVo> list = orderQueryService.queryActiveList(userId, asset, symbol);
		return list;
	}

	void sendOrderMsg(BBOrder order){
		OrderPendingNewMsg msg = new OrderPendingNewMsg();
		msg.setAccountId(order.getUserId());
		msg.setAsset(order.getAsset());
		msg.setBidFlag(order.getBidFlag());
		msg.setCloseFlag(order.getBidFlag());
		msg.setDisplayNumber(order.getVolume());
		msg.setNumber(order.getVolume().subtract(order.getFilledVolume()));
		msg.setOrderId(order.getId());
		msg.setPrice(order.getPrice());
		msg.setSymbol(order.getSymbol());
		msg.setOrderType(order.getOrderType());
		msg.setOrderTime(order.getCreated());
		msg.setTimeInForce(order.getTimeInForce());
		matchMqSender.sendPendingNew(msg);
	}
}
