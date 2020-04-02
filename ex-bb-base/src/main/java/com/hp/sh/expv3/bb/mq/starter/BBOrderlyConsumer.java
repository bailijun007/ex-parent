
package com.hp.sh.expv3.bb.mq.starter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;

import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.constant.MqTopic;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.exceptions.ReSendException;
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
	
	@Value("${bb.mq.consumer.groupId:1}")
	private Integer bbGroupId;
	
	private final Map<String,DefaultMQPushConsumer> mqMap = new LinkedHashMap<String,DefaultMQPushConsumer>();
	
	@Scheduled(cron = "0 * * * * ?")
	@PostConstruct
	public void start123() throws MQClientException{
		List<BBSymbolVO> pcList = this.metadataService.getAllBBContract();
	
		logger.debug("更新MQ监听,{},{},{}", pcList.size(), this.bbGroupId, this.setting.getInstanceName());
		
		Map<String, BBSymbolVO> symbolMap = new HashMap<String, BBSymbolVO>();
		for(BBSymbolVO bbvo : pcList){
			if(bbGroupId==-1 || bbvo.getBbGroupId().equals(this.bbGroupId)){
				String topic = MqTopic.getMatchTopic(bbvo.getAsset(), bbvo.getSymbol());
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
		
		Set<Entry<String, BBSymbolVO>> entrySet = symbolMap.entrySet();
		for(Entry<String, BBSymbolVO> entry : entrySet){
			String topic = entry.getKey();
			BBSymbolVO symbolVO = entry.getValue();
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

	private DefaultMQPushConsumer buildConsumer(String topic) throws MQClientException{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup()+"-"+topic);
        
        consumer.setNamesrvAddr(setting.getNamesrvAddr());
        consumer.setNamespace(setting.getNamespace());
        consumer.setInstanceName(setting.getInstanceName());
        
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, subExpression(MqTags.TAGS_CANCELLED, MqTags.TAGS_NOT_MATCHED, MqTags.TAGS_MATCHED, MqTags.TAGS_TRADE));
        
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
        			logger.error("未知捕获1,{}", e.toString(), e);
        			logger.error("未知捕获2,{},{}", cause.getMessage(), cause.toString(), cause);
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
