/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.config;

import com.hp.sh.expv3.match.component.rocketmq.BasePcAccountContractProducer;
import com.hp.sh.expv3.match.component.rocketmq.BasePcOrderProducer;
import com.hp.sh.expv3.match.config.setting.PcRocketMqSetting;
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
    private PcRocketMqSetting pcRocketMqSetting;
    @Autowired
    private RocketMqSetting rocketMqSetting;

    @Bean(PcmatchConst.MODULE_NAME + "OrderProducer")
    BasePcOrderProducer buildOrderProducer() throws MQClientException {
        BasePcOrderProducer producer = new BasePcOrderProducer();
        producer.setNamespace(rocketMqSetting.getNameSpace());
        producer.setProducerGroup(pcRocketMqSetting.getPcOrderProducerGroupName());
        producer.setNamesrvAddr(rocketMqSetting.getNameSrvAddr());
        producer.setInstanceName(pcRocketMqSetting.getPcOrderProducerInstanceName());
        producer.setVipChannelEnabled(false);
        producer.setDefaultTopicQueueNums(1);
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        logger.info(PcmatchConst.MODULE_NAME + "OrderProducer defaultProducer Started.");
        return producer;
    }

    @Bean(PcmatchConst.MODULE_NAME + "AccountContractProducer")
    BasePcAccountContractProducer buildAccountContractProducer() throws MQClientException {
        BasePcAccountContractProducer producer = new BasePcAccountContractProducer();
        producer.setNamespace(rocketMqSetting.getNameSpace());
        producer.setProducerGroup(pcRocketMqSetting.getPcAccountContractProducerGroupName());
        producer.setNamesrvAddr(rocketMqSetting.getNameSrvAddr());
        producer.setInstanceName(pcRocketMqSetting.getPcAccountContractProducerInstanceName());
        producer.setVipChannelEnabled(false);
        producer.setDefaultTopicQueueNums(8);
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        logger.info(PcmatchConst.MODULE_NAME + "AccountContractProducer defaultProducer Started.");
        return producer;
    }

}