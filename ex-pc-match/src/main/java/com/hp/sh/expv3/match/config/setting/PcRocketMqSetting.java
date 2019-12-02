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
public class PcRocketMqSetting {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private String pcOrderTopicNamePattern;
    private String pcOrderProducerGroupName;
    private String pcOrderProducerInstanceName;

    private String pcAccountContractTopicNamePattern;
    private String pcAccountContractProducerGroupName;
    private String pcAccountContractProducerInstanceName;

    public String getPcAccountContractProducerGroupName() {
        return pcAccountContractProducerGroupName;
    }

    public void setPcAccountContractProducerGroupName(String pcAccountContractProducerGroupName) {
        this.pcAccountContractProducerGroupName = pcAccountContractProducerGroupName;
    }

    public String getPcAccountContractProducerInstanceName() {
        return pcAccountContractProducerInstanceName;
    }

    public void setPcAccountContractProducerInstanceName(String pcAccountContractProducerInstanceName) {
        this.pcAccountContractProducerInstanceName = pcAccountContractProducerInstanceName;
    }

    public String getPcAccountContractTopicNamePattern() {
        return pcAccountContractTopicNamePattern;
    }

    public void setPcAccountContractTopicNamePattern(String pcAccountContractTopicNamePattern) {
        this.pcAccountContractTopicNamePattern = pcAccountContractTopicNamePattern;
    }

    public Logger getLogger() {
        return logger;
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

    public String getPcOrderTopicNamePattern() {
        return pcOrderTopicNamePattern;
    }

    public void setPcOrderTopicNamePattern(String pcOrderTopicNamePattern) {
        this.pcOrderTopicNamePattern = pcOrderTopicNamePattern;
    }
}