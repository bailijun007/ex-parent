package com.hp.sh.expv3.pc.mq;

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

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.constant.MqTopic;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.exceptions.ReSendException;
import com.hp.sh.rocketmq.impl.EndpointContext;

@Configuration
public class OrderlyConsumer {
	private final Logger logger = LoggerFactory.getLogger(OrderlyConsumer.class);
	
	@Autowired
	private RocketmqServerSetting setting;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private EndpointContext endpointContext;
	
	@Value("${pc.mq.consumer.contractGroup}")
	private Integer contractGroup;
	
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
        			logger.error(cause.toString(), e);
        			return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
        		}
        		
        	}
        	
        });
        
        consumer.start();
        return consumer;
	}
	
	@PostConstruct
	public void start123() throws MQClientException{
		List<PcContractVO> pcList = this.metadataService.getAllPcContract();
		for(PcContractVO pc : pcList){
			if(contractGroup.equals(pc.getContractGroup())){
				this.buildConsumer(MqTopic.getMatchTopic(pc.getAsset(), pc.getSymbol()));
			}
	        logger.info("Consumer Started. asset={}, symbol={}", pc.getAsset(), pc.getSymbol());
		}
	}
	

}
