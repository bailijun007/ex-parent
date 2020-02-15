package com.hp.sh.expv3.bb.mq.match;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.job.TradeJob;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.bb.module.trade.service.BBMatchedTradeService;
import com.hp.sh.expv3.bb.mq.match.msg.BBMatchNotMatchMsg;
import com.hp.sh.expv3.bb.mq.match.msg.BbOrderCancelMqMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
public class MatchMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer.class);

	@Autowired
	private BBOrderService orderService;
	@Autowired
	private BBMatchedTradeService matchedTradeService;
	
	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(100);
	private ExecutorService pool = new ThreadPoolExecutor(1, 20, 300L, TimeUnit.SECONDS, queue);
	
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(BBMatchNotMatchMsg msg){
		logger.info("收到撮合未成消息:{}", msg);
		Runnable task = new Runnable(){
			public void run(){
				orderService.setNewStatus(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
			}};
		pool.submit(task);
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_CANCELLED)
	public void handleCancelledMsg(BbOrderCancelMqMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		this.orderService.setCancelled(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
	}
	
	/**
	 * 撮合成功
	 */
	@MQListener(tags=MqTags.TAGS_MATCHED)
	public void handleMatchedTrade(BBMatchedTrade matchedTrade){
		logger.info("收到消息:{}", matchedTrade);
		
		boolean exist = this.matchedTradeService.exist(matchedTrade.getMkOrderId(), matchedTrade.getTkOrderId());
		if(exist){
			logger.warn("撮合已存在:{}", matchedTrade);
			return;
		}
		
		//保存
		matchedTrade.setTakerHandleStatus(IntBool.NO);
		matchedTrade.setMakerHandleStatus(IntBool.NO);
		this.matchedTradeService.save(matchedTrade);
		
		tradeJob.handleMatchedTrade(matchedTrade);
		
	}
	
	@Autowired
	private TradeJob tradeJob;
    
}
