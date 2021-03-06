
package com.hp.sh.expv3.bb.mq.starter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;

import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.constant.MqTopic;
import com.hp.sh.expv3.bb.mq.listen.mq.MatchMqConsumer;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.impl.EndpointContext;

@Order
@ConditionalOnProperty(name="mq.orderly.consumer.enable", havingValue="true")
@Configuration
public class BBOrderlyConsumer {
	private static final Logger logger = LoggerFactory.getLogger(BBOrderlyConsumer.class);
	
	@Autowired
	private RocketmqServerSetting setting;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private EndpointContext endpointContext;
	
	@Value("${bb.mq.consumer.groupId:-1}")
	private Integer groupId;
	
	@Value("${mq.consume.maxThreads:200}")
	private Integer maxThreads;
	
	private final Map<String,DefaultMQPushConsumer> mqMap = new LinkedHashMap<String,DefaultMQPushConsumer>();
	
	private boolean inited = false;

	@Scheduled(cron = "0 * * * * ?")
	public void checkSymbolChange() throws MQClientException{
		if(this.inited){
			this.startConsumer();
		}
	}

	@Order
	@Bean("startOrderlyConsumer123")
	public String start123() throws MQClientException{
		this.startConsumer();
		this.inited = true;
		return null;
	}

	private void startConsumer() throws MQClientException{
		List<BBSymbolVO> symbolList = this.metadataService.getAllBBSymbol();
		
		String subExpression = this.subExpression(MqTags.TAGS_CANCELLED, MqTags.TAGS_NOT_MATCHED, MqTags.TAGS_MATCHED, MqTags.TAGS_TRADE);
	
		logger.debug("更新MQ监听,{},{},{},{}", symbolList.size(), this.groupId, this.setting.getInstanceName(), subExpression);
		
		Set<String> topicSet = new HashSet<String>();
		
		for(BBSymbolVO bbvo : symbolList){
			if(groupId==-1 || bbvo.getBbGroupId().equals(this.groupId)){
				String topic = MqTopic.getMatchTopic(bbvo.getAsset(), bbvo.getSymbol());
				topicSet.add(topic);
			}
		}
		
		for(String topic : new ArrayList<String>(this.mqMap.keySet())){
			DefaultMQPushConsumer mq = this.mqMap.get(topic);
			if(!topicSet.contains(topic)){
				logger.info("关闭监听. topic={}", topic);
				mq.shutdown();
				this.mqMap.remove(topic);
			}
		}
		
		for(String topic : topicSet){
			if(!mqMap.containsKey(topic)){
				logger.info("启动监听MQConsumer. topic={}, subExpression={}", topic, subExpression);
				DefaultMQPushConsumer mq = this.buildConsumer(topic, subExpression);
				this.mqMap.put(topic, mq);
			}
		}
	
		int n = this.mqMap.size();
		logger.debug("mq:{},{}", n, this.mqMap);
		
	}

	private DefaultMQPushConsumer buildConsumer(String topic, String subExpression) throws MQClientException{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup()+"-"+topic);
        
        consumer.setNamesrvAddr(setting.getNamesrvAddr());
        consumer.setNamespace(setting.getNamespace());
        consumer.setInstanceName(setting.getInstanceName());
        
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, subExpression);
        consumer.setConsumeThreadMax(maxThreads);
        
        consumer.registerMessageListener(new MessageListenerOrderly() {

        	@Override
        	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        		logger.debug("收到消息：{},{}", Thread.currentThread().getName()+"-"+Thread.currentThread().getId(), msgs);
        		context.setAutoCommit(true);
        		try{
        			boolean success = endpointContext.consumeMessage(null, msgs);
        			if(success){
        				return ConsumeOrderlyStatus.SUCCESS;
        			}else{
        				return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        			}
        		}catch(Exception e){
        			if(MatchMqConsumer.isResendException(e)){
        				Throwable cause = ExceptionUtils.getRootCause(e);
        				logger.error("未捕获异常 Cause, {}", cause.toString());
        			}else{
        				logger.error("未捕获异常 e, {}", e.getMessage(), e);
        			}
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}
        		
        	}
        	
        });
        
        consumer.start();
        return consumer;
	}
	
	private String subExpression(String...tags) {
		return StringUtils.join(tags, "||");
	}
	
}
