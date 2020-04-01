package com.hp.sh.expv3.config.mq;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hp.sh.rocketmq.config.RocketmqServerSetting;

@Configuration
public class MqConfig {
	
	@Bean
	public MQProducer mqProducer(RocketmqServerSetting setting) throws MQClientException {
		DefaultMQProducer producer = new DefaultMQProducer(setting.getDefaultProducer().getGroup());
	    producer.setNamesrvAddr(setting.getNamesrvAddr());
	    producer.setNamespace(setting.getNamespace());
	    producer.setInstanceName(setting.getInstanceName());
	    producer.setDefaultTopicQueueNums(1);
	    producer.setVipChannelEnabled(false);
		producer.start();
		return producer;
	}

	
}
