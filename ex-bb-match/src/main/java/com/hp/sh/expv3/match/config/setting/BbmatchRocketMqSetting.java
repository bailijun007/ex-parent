/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.BbmatchConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = BbmatchConst.MODULE_NAME + ".rmq")
public class BbmatchRocketMqSetting {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private String bbOrderConsumerGroupName;
    private String bbOrderConsumerInstanceName;
    private long matchPullTimeoutInMs;

    private String bbOrderTopicNamePattern;
    private String bbOrderProducerGroupName;
    private String bbOrderProducerInstanceName;

    private String bbMatchTopicNamePattern;
    private String bbMatchProducerGroupName;
    private String bbMatchProducerInstanceName;

    public Logger getLogger() {
        return logger;
    }

    public String getBbOrderConsumerGroupName() {
        return bbOrderConsumerGroupName;
    }

    public void setBbOrderConsumerGroupName(String bbOrderConsumerGroupName) {
        this.bbOrderConsumerGroupName = bbOrderConsumerGroupName;
    }

    public String getBbOrderConsumerInstanceName() {
        return bbOrderConsumerInstanceName;
    }

    public void setBbOrderConsumerInstanceName(String bbOrderConsumerInstanceName) {
        this.bbOrderConsumerInstanceName = bbOrderConsumerInstanceName;
    }

    public long getMatchPullTimeoutInMs() {
        return matchPullTimeoutInMs;
    }

    public void setMatchPullTimeoutInMs(long matchPullTimeoutInMs) {
        this.matchPullTimeoutInMs = matchPullTimeoutInMs;
    }

    public String getBbOrderTopicNamePattern() {
        return bbOrderTopicNamePattern;
    }

    public void setBbOrderTopicNamePattern(String bbOrderTopicNamePattern) {
        this.bbOrderTopicNamePattern = bbOrderTopicNamePattern;
    }

    public String getBbOrderProducerGroupName() {
        return bbOrderProducerGroupName;
    }

    public void setBbOrderProducerGroupName(String bbOrderProducerGroupName) {
        this.bbOrderProducerGroupName = bbOrderProducerGroupName;
    }

    public String getBbOrderProducerInstanceName() {
        return bbOrderProducerInstanceName;
    }

    public void setBbOrderProducerInstanceName(String bbOrderProducerInstanceName) {
        this.bbOrderProducerInstanceName = bbOrderProducerInstanceName;
    }

    public String getBbMatchTopicNamePattern() {
        return bbMatchTopicNamePattern;
    }

    public void setBbMatchTopicNamePattern(String bbMatchTopicNamePattern) {
        this.bbMatchTopicNamePattern = bbMatchTopicNamePattern;
    }

    public String getBbMatchProducerGroupName() {
        return bbMatchProducerGroupName;
    }

    public void setBbMatchProducerGroupName(String bbMatchProducerGroupName) {
        this.bbMatchProducerGroupName = bbMatchProducerGroupName;
    }

    public String getBbMatchProducerInstanceName() {
        return bbMatchProducerInstanceName;
    }

    public void setBbMatchProducerInstanceName(String bbMatchProducerInstanceName) {
        this.bbMatchProducerInstanceName = bbMatchProducerInstanceName;
    }
}