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
import com.hp.sh.expv3.bb.module.order.service.PcOrderService;
import com.hp.sh.expv3.bb.module.position.service.PcTradeService;
import com.hp.sh.expv3.bb.mq.match.msg.MatchNotMatchMsg;
import com.hp.sh.expv3.bb.mq.match.msg.MatchedOrderCancelledMsg;
import com.hp.sh.expv3.bb.msg.MatchedMsg;
import com.hp.sh.expv3.bb.msg.PcTradeMsg;
import com.hp.sh.rocketmq.annotation.MQListener;

@Component
@MQListener(orderly=MQListener.ORDERLY_YES)
public class MatchMqConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MatchMqConsumer.class);

	@Autowired
	private PcOrderService pcOrderService;
	
	@Autowired
	private PcTradeService pcTradeService;

	private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(100);
	
	private ExecutorService pool = new ThreadPoolExecutor(1, 20, 300L, TimeUnit.SECONDS, queue);
	
	@MQListener(tags=MqTags.TAGS_NOT_MATCHED)
	public void handleNotMatch(MatchNotMatchMsg msg){
		logger.info("收到撮合未成消息:{}", msg);
		Runnable task = new Runnable(){
			public void run(){
				pcOrderService.setNewStatus(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId());
			}};
		pool.submit(task);
	}
	
	//取消订单
	@MQListener(tags=MqTags.TAGS_CANCELLED)
	public void handleCancelledMsg(MatchedOrderCancelledMsg msg){
		logger.info("收到取消订单消息:{}", msg);
		this.pcOrderService.cancel(msg.getAccountId(), msg.getAsset(), msg.getSymbol(), msg.getOrderId(), msg.getCancelNumber());
	}
	
	//成交
	@MQListener(tags=MqTags.TAGS_PC_TRADE)
	public void handleTradeMsg(PcTradeMsg msg){
		logger.info("收到成交消息:{}", msg);
		pcTradeService.handleTradeOrder(msg);
	}
	
	//撮合成功
	@MQListener(tags=MqTags.TAGS_MATCHED)  
	public void handleMatch(MatchedMsg msg){
		logger.info("收到消息:{}", msg);
		
	}
    
}
