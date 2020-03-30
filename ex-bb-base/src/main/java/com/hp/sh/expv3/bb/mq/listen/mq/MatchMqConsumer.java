package com.hp.sh.expv3.bb.mq.listen.mq;

import java.util.List;
import java.util.Map;
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
import com.hp.sh.expv3.bb.job.BBMatchedHandler;
import com.hp.sh.expv3.bb.module.order.service.BBOrderService;
import com.hp.sh.expv3.bb.module.order.service.BBTradeService;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.bb.module.trade.service.BBMatchedTradeService;
import com.hp.sh.expv3.bb.mq.msg.in.BBMatchNotMatchMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BbOrderCancelMqMsg;
import com.hp.sh.expv3.bb.mq.send.BBSender;
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
	@Autowired
	private BBSender sender;
	
	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(100);
	private ExecutorService pool = new ThreadPoolExecutor(1, 20, 300L, TimeUnit.SECONDS, queue);
	
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(BBMatchNotMatchMsg msg){
		logger.info("收到撮合未成交消息:{}", msg);
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
		this.matchedHandler.handleCancelled(msg);
	}
	
	/**
	 * 撮合成功
	 */
	@MQListener(tags=MqTags.TAGS_MATCHED)
	public void handleMatchedTradeList(List<BBMatchedTrade> list){
		logger.info("收到撮合成功消息:{}", list.size());
		
		for(BBMatchedTrade matchedTrade : list){
			matchedTrade.setTakerHandleStatus(IntBool.NO);
			matchedTrade.setMakerHandleStatus(IntBool.NO);
		}
		
		try{
			this.matchedTradeService.save(list);
//			this.sender.send(list);
			for(BBMatchedTrade matchedTrade : list){
				this.matchedHandler.handleMatchedTrade(matchedTrade);
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			for(BBMatchedTrade matchedTrade : list){
				boolean exist = this.matchedTradeService.exist(matchedTrade.getMkOrderId(), matchedTrade.getTkOrderId());
				if(exist){
					logger.warn("撮合已存在:{}", matchedTrade);
					return;
				}
				
				//保存
				matchedTrade.setTakerHandleStatus(IntBool.NO);
				matchedTrade.setMakerHandleStatus(IntBool.NO);
				this.matchedTradeService.save(matchedTrade);
				
				//处理用户成交
				matchedHandler.handleMatchedTrade(matchedTrade);
			}
			
			//通知前端
			sender.send(list);
		}

	}
	
	/**
	 * 撮合成功
	 */
	public void handleMatchedTradeOne(BBMatchedTrade matchedTrade){
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
		
		//通知前端
//		sender.send(matchedTrade);
		
		//处理用户成交
		matchedHandler.handleMatchedTrade(matchedTrade);
		
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_TRADE)
	public void handleTradeMsg(BBTradeVo msg){
		logger.info("收到用户成交消息:{}", msg);
		this.tradeService.handleTrade(msg);
	}
	
	@Autowired
	private BBTradeService tradeService;
	
	@Autowired
	private BBMatchedHandler matchedHandler;
    
}
