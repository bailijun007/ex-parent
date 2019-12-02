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

    public Logger getLogger() {
        return logger;
    }

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
}