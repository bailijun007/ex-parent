package com.hp.sh.expv3.pc.trade.mq.starter;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.pc.trade.constant.MsgConstant;
import com.hp.sh.expv3.pc.trade.pojo.BBSymbol;
import com.hp.sh.expv3.pc.trade.service.impl.SupportBbGroupIdsJobServiceImpl;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.exceptions.ReSendException;
import com.hp.sh.rocketmq.impl.EndpointContext;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;

@Configuration
public class MqOrderlyConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MqOrderlyConsumer.class);
	
	@Autowired
	private RocketmqServerSetting setting;

    @Autowired
    private SupportBbGroupIdsJobServiceImpl supportBbGroupIdsJobService;

	@Autowired
	private EndpointContext endpointContext;
	
	@Value("${pc.trade.bbGroupIds}")
	private Integer bbGroupId;
	
	private Map<String,DefaultMQPushConsumer> mqMap = new LinkedHashMap<String,DefaultMQPushConsumer>();
	
	private DefaultMQPushConsumer buildConsumer(String topic) throws MQClientException{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup()+"-"+topic);
        
        consumer.setNamesrvAddr(setting.getNamesrvAddr());
        consumer.setNamespace(setting.getNamespace());
        consumer.setInstanceName(setting.getInstanceName());
        
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, MsgConstant.TAG_BB_MATCH);
        
        consumer.registerMessageListener(new MessageListenerOrderly() {

        	@Override
        	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        		logger.debug("收到消息：{},{}", Thread.currentThread().getId(), msgs);
        		context.setAutoCommit(true);
        		try{
        			boolean success = endpointContext.consumeMessage(null, msgs);
        			if(success){
        				return ConsumeOrderlyStatus.SUCCESS;
        			}else{
        				return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        			}
        		}catch(ReSendException re){
        			Throwable cause = ExceptionUtils.getRootCause(re);
        			logger.error(cause.getMessage(), cause);
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}catch(ExException e){
        			Throwable cause = ExceptionUtils.getRootCause(e);
        			logger.error(e.toString(), cause);
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}catch(ExSysException e){
        			Throwable cause = ExceptionUtils.getRootCause(e);
        			logger.error(e.toString(), cause);
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}catch(UpdateException e){
        			Throwable cause = ExceptionUtils.getRootCause(e);
        			logger.warn(cause.toString(), cause);
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}catch(Exception e){
        			Throwable cause = ExceptionUtils.getRootCause(e);
        			logger.error("未知捕获,{}", e.toString(), e);
        			logger.error("未知捕获,{}", cause.getMessage(), cause);
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}
        		
        	}
        	
        });
        
        consumer.start();
        return consumer;
	}
	
	@Scheduled(cron = "0 * * * * ?")
	@PostConstruct
	public void start123() throws MQClientException{
		List<BBSymbol> pcList = supportBbGroupIdsJobService.getSymbols();

		logger.debug("更新MQ监听,{},{},{}", pcList.size(), this.bbGroupId, this.setting.getInstanceName());
		
		Map<String, BBSymbol> symbolMap = new HashMap<String, BBSymbol>();
		for(BBSymbol bbvo : pcList){
			if(bbvo.getBbGroupId().equals(this.bbGroupId)){
				String topic = MsgConstant.getMatchTopic(bbvo.getAsset(), bbvo.getSymbol());
				symbolMap.put(topic, bbvo);
			}
		}
		
		for(String topic : new ArrayList<String>(this.mqMap.keySet())){
			DefaultMQPushConsumer mq = this.mqMap.get(topic);
			if(!symbolMap.containsKey(topic)){
				logger.info("关闭监听. topic={}", topic);
				mq.shutdown();
				this.mqMap.remove(topic);
			}
		}
		
		Set<Entry<String, BBSymbol>> entrySet = symbolMap.entrySet();
		for(Entry<String, BBSymbol> entry : entrySet){
			String topic = entry.getKey();
            BBSymbol symbolVO = entry.getValue();
			if(!mqMap.containsKey(topic)){
				if(symbolVO.getBbGroupId().equals(this.bbGroupId)){
					logger.info("启动监听MQConsumer. asset={}, symbol={}", symbolVO.getAsset(), symbolVO.getSymbol());
					DefaultMQPushConsumer mq = this.buildConsumer(topic);
					this.mqMap.put(topic, mq);
				}
			}
		}

		int n = this.mqMap.size();
		logger.debug("mq:{},{}", n, this.mqMap);
		return;
		
	}
	

}
