package com.hp.sh.expv3.bb.mq.starter;

import java.util.List;

import javax.annotation.PostConstruct;

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

import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBSymbolVO;
import com.hp.sh.expv3.bb.constant.MqTopic;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.exceptions.ReSendException;
import com.hp.sh.rocketmq.impl.EndpointContext;

@Configuration
public class MqOrderlyConsumer {
	private static final Logger logger = LoggerFactory.getLogger(MqOrderlyConsumer.class);
	
	@Autowired
	private RocketmqServerSetting setting;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private EndpointContext endpointContext;
	
	@Value("${pc.mq.consumer.bbGroup}")
	private Integer bbGroup;
	
	private DefaultMQPushConsumer buildConsumer(String topic) throws MQClientException{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup()+"-"+topic);
        
        consumer.setNamesrvAddr(setting.getNamesrvAddr());
        consumer.setNamespace(setting.getNamespace());
        consumer.setInstanceName(setting.getInstanceName());
        
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        consumer.subscribe(topic, "*");
        
        consumer.registerMessageListener(new MessageListenerOrderly() {

        	@Override
        	public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        		logger.info("收到消息：{}", msgs);
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
        		}catch(Exception e){
        			Throwable cause = ExceptionUtils.getRootCause(e);
        			logger.error(cause.toString(), cause);
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}
        		
        	}
        	
        });
        
        consumer.start();
        return consumer;
	}
	
	@PostConstruct
	public void start123() throws MQClientException{
		List<BBSymbolVO> pcList = this.metadataService.getAllBBContract();
		for(BBSymbolVO bbvo : pcList){
			if(bbGroup.equals(bbvo.getBbGroup())){
				this.buildConsumer(MqTopic.getMatchTopic(bbvo.getAsset(), bbvo.getSymbol()));
			}
	        logger.info("MQConsumer Started. asset={}, symbol={}", bbvo.getAsset(), bbvo.getSymbol());
		}
	}
	

}
