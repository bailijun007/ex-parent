/**
 * @author 10086
 * @date 2019/10/21
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.PcmatchConst;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = PcmatchConst.MODULE_NAME + ".rmq")
public class PcmatchRocketMqSetting {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private String pcOrderConsumerGroupName;
    private String pcOrderConsumerInstanceName;
    private long matchPullTimeoutInMs;

    private String pcOrderTopicNamePattern;
    private String pcOrderProducerGroupName;
    private String pcOrderProducerInstanceName;

    private String pcMatchTopicNamePattern;
    private String pcMatchProducerGroupName;
    private String pcMatchProducerInstanceName;

    public String getPcOrderConsumerGroupName() {
        return pcOrderConsumerGroupName;
    }

    public void setPcOrderConsumerGroupName(String pcOrderConsumerGroupName) {
        this.pcOrderConsumerGroupName = pcOrderConsumerGroupName;
    }

    public String getPcOrderConsumerInstanceName() {
        return pcOrderConsumerInstanceName;
    }

    public void setPcOrderConsumerInstanceName(String pcOrderConsumerInstanceName) {
        this.pcOrderConsumerInstanceName = pcOrderConsumerInstanceName;
    }

    public long getMatchPullTimeoutInMs() {
        return matchPullTimeoutInMs;
    }

    public void setMatchPullTimeoutInMs(long matchPullTimeoutInMs) {
        this.matchPullTimeoutInMs = matchPullTimeoutInMs;
    }

    public String getPcOrderTopicNamePattern() {
        return pcOrderTopicNamePattern;
    }

    public void setPcOrderTopicNamePattern(String pcOrderTopicNamePattern) {
        this.pcOrderTopicNamePattern = pcOrderTopicNamePattern;
    }

    public String getPcOrderProducerGroupName() {
        return pcOrderProducerGroupName;
    }

    public void setPcOrderProducerGroupName(String pcOrderProducerGroupName) {
        this.pcOrderProducerGroupName = pcOrderProducerGroupName;
    }

    public String getPcOrderProducerInstanceName() {
        return pcOrderProducerInstanceName;
    }

    public void setPcOrderProducerInstanceName(String pcOrderProducerInstanceName) {
        this.pcOrderProducerInstanceName = pcOrderProducerInstanceName;
    }

    public String getPcMatchTopicNamePattern() {
        return pcMatchTopicNamePattern;
    }

    public void setPcMatchTopicNamePattern(String pcMatchTopicNamePattern) {
        this.pcMatchTopicNamePattern = pcMatchTopicNamePattern;
    }

    public String getPcMatchProducerGroupName() {
        return pcMatchProducerGroupName;
    }

    public void setPcMatchProducerGroupName(String pcMatchProducerGroupName) {
        this.pcMatchProducerGroupName = pcMatchProducerGroupName;
    }

    public String getPcMatchProducerInstanceName() {
        return pcMatchProducerInstanceName;
    }

    public void setPcMatchProducerInstanceName(String pcMatchProducerInstanceName) {
        this.pcMatchProducerInstanceName = pcMatchProducerInstanceName;
    }
}