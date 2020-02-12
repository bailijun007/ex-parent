package com.hp.sh.expv3.bb.job;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.TradeRoles;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.bb.module.trade.service.BBMatchedTradeService;
import com.hp.sh.expv3.bb.msg.BBTradeMsg;
import com.hp.sh.expv3.utils.DbDateUtils;

@Component
public class TradeJob {
    private static final Logger logger = LoggerFactory.getLogger(TradeJob.class);

    @Autowired
    private BBMatchedTradeService matchedTradeService;
    
	@Autowired
	private BBTradeService tradeService;

	private BlockingQueue<Runnable> _queue = new LinkedBlockingQueue<Runnable>(100);
	private ExecutorService pool = new ThreadPoolExecutor(1, 20, 300L, TimeUnit.SECONDS, _queue);
	
	private boolean running = false;
	
	@Scheduled(cron = "0 0/10 * * * ?")
	public synchronized void handle() {
		Long now = DbDateUtils.now();
		Long startTime = now-1000*3600;
		Page page = new Page(1, 100, 1000L);
		Long startId = null;
		while(true){
			List<BBMatchedTrade> list = this.matchedTradeService.queryPending(page, null, startTime, startId);
			
			if(list==null || list.isEmpty()){
				break;
			}
			
			for(BBMatchedTrade matchedTrade : list){
				pool.submit(new Runnable(){
					public void run() {
						try{
							handleMatchedTrade(matchedTrade);
						}catch(Exception e){
							logger.error(e.getMessage(), e);
						}
					}
					
				});
				startId = matchedTrade.getId();
			}
			
			page.setPageNo(page.getPageNo()+1);
		}
	}

	public void handleMatchedTrade(BBMatchedTrade matchedTrade){
		// MAKER
		BBTradeMsg makerTradeVo = new BBTradeMsg();
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

		//成交
		tradeService.handleTrade(makerTradeVo);
		
		//修改态
		matchedTradeService.setMakerHandleStatus(matchedTrade.getId());
		
		// TAKER
		BBTradeMsg takerTradeVo = new BBTradeMsg();
		takerTradeVo.setTradeId(matchedTrade.getId());
		takerTradeVo.setMakerFlag(TradeRoles.TAKER);
		takerTradeVo.setAsset(matchedTrade.getAsset());
		takerTradeVo.setSymbol(matchedTrade.getSymbol());
		takerTradeVo.setPrice(matchedTrade.getPrice());
		takerTradeVo.setNumber(matchedTrade.getNumber());
		takerTradeVo.setTradeTime(matchedTrade.getTradeTime());
		
		takerTradeVo.setAccountId(matchedTrade.getTkAccountId());
		takerTradeVo.setMatchTxId(matchedTrade.getMatchTxId());
		takerTradeVo.setOrderId(matchedTrade.getTkOrderId());
		
		takerTradeVo.setOpponentOrderId(matchedTrade.getMkOrderId());

		//成交
		tradeService.handleTrade(takerTradeVo);
		
		//修改态
		matchedTradeService.setTakerHandleStatus(matchedTrade.getId());
		
	}

}
