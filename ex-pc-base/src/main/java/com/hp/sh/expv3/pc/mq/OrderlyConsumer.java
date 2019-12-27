package com.hp.sh.expv3.pc.mq;

import java.util.List;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerOrderly;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.expv3.pc.constant.MqTopic;
import com.hp.sh.rocketmq.config.RocketmqServerSetting;
import com.hp.sh.rocketmq.impl.EndpointContext;

@Configuration
public class OrderlyConsumer {
	
	@Autowired
	private RocketmqServerSetting setting;
	
	@Autowired
	private EndpointContext endpointContext;
	
	public DefaultMQPushConsumer buildConsumer(String topic) throws MQClientException{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(setting.getDefaultConsumer().getGroup());

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
		DefaultMQPushConsumer consumer = this.buildConsumer(MqTopic.TOPIC_PCMATCH_BTC__BTC_USD);
        consumer.start();
        System.out.printf("Consumer Started.%n");
        return "";
	}
	

}
