package com.hp.sh.expv3.bb.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.executor.orderly.OrderlyExecutors;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.TradeRoles;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.bb.module.trade.service.BBMatchedTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BbOrderCancelMqMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradePair;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.component.executor.AbstractGroupTask;
import com.hp.sh.expv3.dev.LimitTimeHandle;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;

@Component
public class BBMatchedHandler {
    private static final Logger logger = LoggerFactory.getLogger(BBMatchedHandler.class);

    @Autowired
    private BBMatchedTradeService matchedTradeService;
    
	@Autowired
	private BBTradeService tradeService;
	
	@Autowired
	private BBOrderService orderService;
	
	@Autowired
	private OrderlyExecutors tradeExecutors;
	
	@LimitTimeHandle
	public void handlePending() {
		Long startTime = DbDateUtils.now()-1000*3600;
		Page page = new Page(1, 50, 1000L);
		Long startId = null;
		while(true){
			List<BBMatchedTrade> list = this.matchedTradeService.queryPending(page, null, startTime, startId);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(BBMatchedTrade matchedTrade : list){
				try{
					handleMatchedTrade(matchedTrade);
				}catch(Exception e){
					logger.error(e.getMessage(), e);
				}
				startId = matchedTrade.getId();
			}
			
		}
		
	}

	public void handleMatchedTrade(BBMatchedTrade matchedTrade){
		BBTradePair tradePair = this.getTradePair(matchedTrade);
		
		// MAKER
		if(matchedTrade.getMakerHandleStatus()==BBMatchedTrade.NO){
			BBTradeVo tradeVo = tradePair.getMakerTradeVo();
			this.tradeExecutors.submit(new TradeTask(tradeVo));
		}
		
		// TAKER
		if(matchedTrade.getTakerHandleStatus()==BBMatchedTrade.NO){
			BBTradeVo tradeVo = tradePair.getTakerTradeVo();
			this.tradeExecutors.submit(new TradeTask(tradeVo));
		}
		
	}
	
	public void handleCancelled(BbOrderCancelMqMsg msg){
		this.tradeExecutors.submit(new CancelledTask(msg));
	}
	
	private BBTradePair getTradePair(BBMatchedTrade matchedTrade){
		// MAKER
		BBTradeVo makerTradeVo = new BBTradeVo();
		makerTradeVo.setTradeId(matchedTrade.getId());
		makerTradeVo.setMakerFlag(TradeRoles.MAKER);
		makerTradeVo.setAsset(matchedTrade.getAsset());
		makerTradeVo.setSymbol(matchedTrade.getSymbol());
		makerTradeVo.setPrice(matchedTrade.getPrice());
		makerTradeVo.setNumber(matchedTrade.getNumber());
		makerTradeVo.setTradeTime(matchedTrade.getTradeTime());
		
		makerTradeVo.setAccountId(matchedTrade.getMkAccountId());
		makerTradeVo.setOrderId(matchedTrade.getMkOrderId());
		makerTradeVo.setMatchTxId(matchedTrade.getMatchTxId());
		
		makerTradeVo.setOpponentOrderId(matchedTrade.getTkOrderId());

		// TAKER
		BBTradeVo takerTradeVo = new BBTradeVo();
		takerTradeVo.setTradeId(matchedTrade.getId());
		takerTradeVo.setMakerFlag(TradeRoles.TAKER);
		takerTradeVo.setAsset(matchedTrade.getAsset());
		takerTradeVo.setSymbol(matchedTrade.getSymbol());
		takerTradeVo.setPrice(matchedTrade.getPrice());
		takerTradeVo.setNumber(matchedTrade.getNumber());
		takerTradeVo.setTradeTime(matchedTrade.getTradeTime());
		
		takerTradeVo.setAccountId(matchedTrade.getTkAccountId());
		takerTradeVo.setOrderId(matchedTrade.getTkOrderId());
		takerTradeVo.setMatchTxId(matchedTrade.getMatchTxId());
		
		takerTradeVo.setOpponentOrderId(matchedTrade.getMkOrderId());

		return new BBTradePair(makerTradeVo, takerTradeVo);
	}

	class TradeTask extends AbstractGroupTask{

		private BBTradeVo tradeVo;
		
		public TradeTask(BBTradeVo tradeVo) {
			this.tradeVo = tradeVo;
		}
		
		public Object getGroupId(){
			int key = tradeVo.getOrderId().hashCode();
			return Math.abs(key);
		}

		public void doRun() {
			try{
				//成交
				tradeService.handleTrade(tradeVo);
				
				//修改态
				if(tradeVo.getMakerFlag()==IntBool.YES){
					matchedTradeService.setMakerHandleStatus(tradeVo.getTradeId());
				}else{
					matchedTradeService.setTakerHandleStatus(tradeVo.getTradeId());
				}
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}

	}

	class CancelledTask extends AbstractGroupTask{

		private BbOrderCancelMqMsg msg;
		
		public CancelledTask(BbOrderCancelMqMsg msg) {
			this.msg = msg;
		}
		
		public Object getGroupId(){
			int key = msg.getOrderId().hashCode();
			return Math.abs(key);
		}

		public void doRun() {
			try{
				orderService.setCancelled(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}

	}
	
}
