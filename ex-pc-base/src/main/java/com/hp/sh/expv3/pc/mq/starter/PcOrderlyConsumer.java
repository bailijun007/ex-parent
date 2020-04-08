package com.hp.sh.expv3.pc.mq.starter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;

import com.gitee.hupadev.commons.bean.BeanHelper;
import com.gitee.hupadev.commons.mybatis.ex.UpdateException;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.constant.MqTopic;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.exceptions.ReSendException;
import com.hp.sh.rocketmq.impl.EndpointContext;

@Order
@Configuration
public class PcOrderlyConsumer {
	private final Logger logger = LoggerFactory.getLogger(PcOrderlyConsumer.class);
	
	@Autowired
	private RocketmqServerSetting setting;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private EndpointContext endpointContext;
	
	@Value("${pc.mq.consumer.groupId:-1}")
	private Integer groupId;

	private final Map<String,DefaultMQPushConsumer> mqMap = new LinkedHashMap<String,DefaultMQPushConsumer>();
	
	@Scheduled(cron = "0 * * * * ?")
	@PostConstruct
	public void start123() throws MQClientException{
		List<PcContractVO> pcList = this.metadataService.getAllPcContract();
		
		String subExpression = subExpression(MqTags.TAGS_CANCELLED, MqTags.TAGS_NOT_MATCHED, MqTags.TAGS_MATCHED, MqTags.TAGS_PC_TRADE, MqTags.TAGS_ORDER_ALL_CANCELLED);
	
		logger.debug("更新MQ监听,{},{},{},{}", pcList.size(), this.groupId, this.setting.getInstanceName(), subExpression);
		
		Set<String> topicSet = new HashSet<String>();
		
		for(PcContractVO bbvo : pcList){
			if(groupId==-1 || bbvo.getContractGroup().equals(this.groupId)){
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
				logger.info("启动监听MQConsumer. topic={},subExpression={}", topic, subExpression);
				DefaultMQPushConsumer mq = this.buildConsumer(topic, subExpression);
				this.mqMap.put(topic, mq);
			}
		}
	
		int n = this.mqMap.size();
		logger.debug("mq:{},{}", n, this.mqMap);
		return;
		
	}

	private DefaultMQPushConsumer buildConsumer(String topic, String subExpression) throws MQClientException{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup()+"-"+topic);
//        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup()+"-"+topic, null, new PcAllocateMessageQueueStrategy());
        
        consumer.setNamesrvAddr(setting.getNamesrvAddr());
        consumer.setNamespace(setting.getNamespace());
        consumer.setInstanceName(setting.getInstanceName());
        
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, subExpression);
        
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
        			if(cause instanceof UpdateException){
        				logger.warn("更新失败："+cause.toString());
        			}else{
            			logger.error("未知捕获1,{}", e.getMessage(), e);
        			}
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}
        		
        	}
        	
        });
        
        consumer.start();
        return consumer;
	}
	
	private String subExpression() {
		List<String> list = BeanHelper.getDistinctPropertyList(this.endpointContext.getConfigList(), "tags");
		String exp = StringUtils.join(list, "||");
		return exp;
	}
	
	private String subExpression(String...tags) {
		return StringUtils.join(tags, "||");
	}

}
