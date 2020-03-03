package com.hp.sh.expv3.bb.job;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.TradeRoles;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.bb.module.trade.service.BBMatchedTradeService;
import com.hp.sh.expv3.bb.strategy.vo.BBTradePair;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.component.executor.OrderlyExecutors;
import com.hp.sh.expv3.utils.DbDateUtils;

@Component
public class BBMatchedHandler {
    private static final Logger logger = LoggerFactory.getLogger(BBMatchedHandler.class);

    @Autowired
    private BBMatchedTradeService matchedTradeService;
    
	@Autowired
	private BBTradeService tradeService;

	private OrderlyExecutors orderlyExecutors = new OrderlyExecutors(10);
	
	public void handlePending() {
		Long now = DbDateUtils.now();
		Long startTime = 0L; //now-1000*3600;
		Page page = new Page(1, 100, 1000L);
		Long startId = null;
		while(true){
			List<BBMatchedTrade> list = this.matchedTradeService.queryPending(page, null, startTime, startId);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(BBMatchedTrade matchedTrade : list){
				handleMatchedTrade(matchedTrade);
				startId = matchedTrade.getId();
			}
			
		}
	}

	public void handleMatchedTrade(BBMatchedTrade matchedVo){
		BBTradePair tradePair = this.getTradePair(matchedVo);
		
		// MAKER
		BBTradeVo makerTradeVo = tradePair.getMakerTradeVo();
		this.orderlyExecutors.submit(makerTradeVo.getAccountId(), new TradeTask(makerTradeVo));
		
		// TAKER
		BBTradeVo takerTradeVo = tradePair.getTakerTradeVo();
		this.orderlyExecutors.submit(takerTradeVo.getAccountId(), new TradeTask(takerTradeVo));
		
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
		makerTradeVo.setMatchTxId(matchedTrade.getMatchTxId());
		makerTradeVo.setOrderId(matchedTrade.getMkOrderId());
		
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
		takerTradeVo.setMatchTxId(matchedTrade.getMatchTxId());
		makerTradeVo.setOrderId(matchedTrade.getTkOrderId());
		
		takerTradeVo.setOpponentOrderId(matchedTrade.getMkOrderId());

		return new BBTradePair(makerTradeVo, makerTradeVo);
	}

	class TradeTask implements Runnable{

		private BBTradeVo tradeVo;
		
		public TradeTask(BBTradeVo tradeVo) {
			this.tradeVo = tradeVo;
		}

		@Override
		public void run() {
			try{
				//成交
				tradeService.handleTrade(tradeVo);
				//修改态
				matchedTradeService.setMakerHandleStatus(tradeVo.getTradeId());
			}catch(Exception e){
				logger.error(e.getMessage(), e);
			}
		}

	}
	
}
