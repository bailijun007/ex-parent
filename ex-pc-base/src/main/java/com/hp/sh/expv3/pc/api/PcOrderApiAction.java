package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.module.account.service.PcAccountCoreService;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.service.PcOrderQueryService;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.mq.MatchMqSender;
import com.hp.sh.expv3.pc.mq.consumer.msg.BookResetMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.OrderPendingCancelMsg;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.vo.ExPageResult;
import com.hp.sh.expv3.pc.vo.response.ActiveOrderVo;
import com.hp.sh.expv3.utils.CheckUtils;

@RestController
public class PcOrderApiAction implements PcOrderApi {

	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Autowired
	private PcOrderService pcOrderService;

	@Autowired
	private PcOrderQueryService orderQueryService;
	
	@Autowired
	private MatchMqSender matchMqSender;
	
	@Autowired
	private PcStrategyContext strategyContext;
	
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
	public Long create(Long userId, String asset, String symbol, Integer closeFlag, Integer longFlag, Integer timeInForce, BigDecimal price, BigDecimal number, String cliOrderId){
		
		CheckUtils.checkPositiveNum(price, number);
		
		CheckUtils.checkIntBool(closeFlag, longFlag);
		
		PcOrder order = pcOrderService.create(userId, cliOrderId, asset, symbol, closeFlag, longFlag, timeInForce, price, number);

		//send mq
		matchMqSender.sendPendingNew(order);
		
		return order.getId();
	}
	
	@Override
	public void cancel(Long userId, String asset, String symbol, Long orderId) {

		boolean ok = this.pcOrderService.setPendingCancel(userId, asset, symbol, orderId);

		if(!ok){
			return;
		}
		
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
	public void bookReset (String asset, String symbol){
		//发送消息
		BookResetMsg msg = new BookResetMsg(asset, symbol);
		msg.setAsset(asset);
		msg.setSymbol(symbol);
		this.matchMqSender.sendBookResetMsg(msg);
	}
	
	@Override
	public BigDecimal getMaxOpenVolume(Long userId, String asset, String symbol, Long longFlag, BigDecimal leverage){
		BigDecimal balance = this.pcAccountCoreService.getBalance(userId, asset);
		BigDecimal maxOpenVolume = strategyContext.calcMaxOpenVolume(userId, asset, symbol, longFlag, leverage, balance);
		return maxOpenVolume;
	}

	@Override
	public ExPageResult<ActiveOrderVo> queryActiveList(Integer pageSize, Long userId, String asset, String symbol, Long startId){
		ExPageResult<ActiveOrderVo> page = new ExPageResult<ActiveOrderVo>();
		page.setPageNo(1);
		page.setPageSize(pageSize);
		List<ActiveOrderVo> list = orderQueryService.queryUserActiveList(page, userId, asset, symbol, startId);
		page.setList(list);
		return page;
	}
}
