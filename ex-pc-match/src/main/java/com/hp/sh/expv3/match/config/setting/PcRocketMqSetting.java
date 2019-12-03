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

import javax.annotation.PostConstruct;

@Configuration
@ConfigurationProperties(prefix = PcmatchConst.MODULE_NAME + ".rmq")
public class PcRocketMqSetting {

    final Logger logger = LoggerFactory.getLogger(getClass());

    private String pcOrderTopicNamePattern;
    private String pcOrderProducerGroupName;
    private String pcOrderProducerInstanceName;

    private String pcMatchTopicNamePattern;
    private String pcMatchProducerGroupName;
    private String pcMatchProducerInstanceName;

    @PostConstruct
    private void init(){
//        System.out.println(this.getPcMatchProducerInstanceName());
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

    public String getPcMatchTopicNamePattern() {
        return pcMatchTopicNamePattern;
    }

    public void setPcMatchTopicNamePattern(String pcMatchTopicNamePattern) {
        this.pcMatchTopicNamePattern = pcMatchTopicNamePattern;
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