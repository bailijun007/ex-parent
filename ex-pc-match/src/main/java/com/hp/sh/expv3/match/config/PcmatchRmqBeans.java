/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.config;

import com.hp.sh.expv3.match.component.rocketmq.PcMatchProducer;
import com.hp.sh.expv3.match.component.rocketmq.BasePcOrderProducer;
import com.hp.sh.expv3.match.config.setting.PcmatchRocketMqSetting;
import com.hp.sh.expv3.match.config.setting.RocketMqSetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class PcmatchRmqBeans {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PcmatchRocketMqSetting pcmatchRocketMqSetting;
    @Autowired
    private RocketMqSetting rocketMqSetting;

    @Bean(PcmatchConst.MODULE_NAME + "OrderProducer")
    BasePcOrderProducer buildOrderProducer() throws MQClientException {
        BasePcOrderProducer producer = new BasePcOrderProducer();
        producer.setNamespace(rocketMqSetting.getNameSpace());
        producer.setProducerGroup(pcmatchRocketMqSetting.getPcOrderProducerGroupName());
        producer.setNamesrvAddr(rocketMqSetting.getNameSrvAddr());
        producer.setInstanceName(pcmatchRocketMqSetting.getPcOrderProducerInstanceName());
        producer.setVipChannelEnabled(false);
        producer.setDefaultTopicQueueNums(1);
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        logger.info(PcmatchConst.MODULE_NAME + "OrderProducer defaultProducer Started.");
        return producer;
    }

    @Bean(PcmatchConst.MODULE_NAME + "AccountContractProducer")
    PcMatchProducer buildAccountContractProducer() throws MQClientException {
        PcMatchProducer producer = new PcMatchProducer();
        producer.setNamespace(rocketMqSetting.getNameSpace());
        producer.setProducerGroup(pcmatchRocketMqSetting.getPcMatchProducerGroupName());
        producer.setNamesrvAddr(rocketMqSetting.getNameSrvAddr());
        producer.setInstanceName(pcmatchRocketMqSetting.getPcMatchProducerInstanceName());
        producer.setVipChannelEnabled(false);
        producer.setDefaultTopicQueueNums(8);
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        logger.info(PcmatchConst.MODULE_NAME + "AccountContractProducer defaultProducer Started.");
        return producer;
    }

}