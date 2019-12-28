package com.hp.sh.expv3.pc.mq;

import java.util.List;

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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.expv3.config.web.ExpMvcConfig;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.constant.MqTopic;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.impl.EndpointContext;

@Configuration
public class OrderlyConsumer {
	private static final Logger logger = LoggerFactory.getLogger(ExpMvcConfig.class);
	
	@Autowired
	private RocketmqServerSetting setting;
	
	@Autowired
	private MetadataService metadataService;
	
	@Autowired
	private EndpointContext endpointContext;
	
	@Value("${pc.mq.consumer.contractGroup}")
	private Integer contractGroup;
	
	public DefaultMQPushConsumer buildConsumer(String topic) throws MQClientException{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup()+"-"+topic);

        consumer.setNamesrvAddr(setting.getNamesrvAddr());
        setting.setNamespace(setting.getNamespace());
        setting.setInstanceName(setting.getInstanceName());
        
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        consumer.subscribe(topic, "*");

        consumer.registerMessageListener(new MessageListenerOrderly() {

            @Override
            public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
                context.setAutoCommit(true);
    			boolean success = endpointContext.consumeMessage(null, msgs);
    			if(success){
    				return ConsumeOrderlyStatus.SUCCESS;
    			}else{
    				return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
    			}
            }
        });
        
        return consumer;
	}
	
	@Bean
	public String start123() throws MQClientException{
		List<PcContractVO> pcList = this.metadataService.getAllPcContract();
		for(PcContractVO pc : pcList){
			if(contractGroup.equals(pc.getContractGroup())){
				DefaultMQPushConsumer consumer = this.buildConsumer(MqTopic.getOrderTopic(pc.getAsset(), pc.getSymbol()));
		        consumer.start();
			}
	        logger.info("Consumer Started. asset={}, symbol={}", pc.getAsset(), pc.getSymbol());
		}
        return "";
	}
	

}
