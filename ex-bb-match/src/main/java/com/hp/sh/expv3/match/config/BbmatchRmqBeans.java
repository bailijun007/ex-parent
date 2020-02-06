/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.config;

import com.hp.sh.expv3.match.component.rocketmq.BaseBbOrderProducer;
import com.hp.sh.expv3.match.component.rocketmq.BbMatchProducer;
import com.hp.sh.expv3.match.config.setting.BbmatchRocketMqSetting;
import com.hp.sh.expv3.match.config.setting.RocketMqSetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
import org.apache.rocketmq.client.exception.MQClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class BbmatchRmqBeans {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbmatchRocketMqSetting bbmatchRocketMqSetting;
    @Autowired
    private RocketMqSetting rocketMqSetting;

    @Bean(BbmatchConst.MODULE_NAME + "OrderProducer")
    BaseBbOrderProducer buildOrderProducer() throws MQClientException {
        BaseBbOrderProducer producer = new BaseBbOrderProducer();
        producer.setNamespace(rocketMqSetting.getNameSpace());
        producer.setProducerGroup(bbmatchRocketMqSetting.getBbOrderProducerGroupName());
        producer.setNamesrvAddr(rocketMqSetting.getNameSrvAddr());
        producer.setInstanceName(bbmatchRocketMqSetting.getBbOrderProducerInstanceName());
        producer.setVipChannelEnabled(false);
        producer.setDefaultTopicQueueNums(1);
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        logger.info("{}OrderProducer {} {} Started.", BbmatchConst.MODULE_NAME, bbmatchRocketMqSetting.getBbOrderProducerGroupName(), bbmatchRocketMqSetting.getBbOrderProducerInstanceName());
        return producer;
    }

    @Bean(BbmatchConst.MODULE_NAME + "AccountSymbolProducer")
    BbMatchProducer buildAccountContractProducer() throws MQClientException {
        BbMatchProducer producer = new BbMatchProducer();
        producer.setNamespace(rocketMqSetting.getNameSpace());
        producer.setProducerGroup(bbmatchRocketMqSetting.getBbMatchProducerGroupName());
        producer.setNamesrvAddr(rocketMqSetting.getNameSrvAddr());
        producer.setInstanceName(bbmatchRocketMqSetting.getBbMatchProducerInstanceName());
        producer.setVipChannelEnabled(false);
        producer.setDefaultTopicQueueNums(8);
        producer.setRetryTimesWhenSendAsyncFailed(3);
        producer.setRetryTimesWhenSendFailed(3);
        producer.start();
        logger.info("{}AccountSymbolProducer {} {} Started.", BbmatchConst.MODULE_NAME, bbmatchRocketMqSetting.getBbOrderProducerGroupName(), bbmatchRocketMqSetting.getBbOrderProducerInstanceName());
        return producer;
    }

}