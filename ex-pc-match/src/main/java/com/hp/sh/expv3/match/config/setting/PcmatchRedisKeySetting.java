/**
 * @author zw
 * @date 2019/8/9
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.PcmatchConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = PcmatchConst.MODULE_NAME + ".rediskey")
public class PcmatchRedisKeySetting {

    private String pcBookChannelRedisKeyPattern;
    private String pcTradeChannelRedisKeyPattern;
    private String pcOrderSnapshotRedisKeyPattern;
    private String pcOrderSentMqOffsetRedisKeyPattern;

    private String pcOrderMatchedQueueRedisKeyPattern;
    private String pcOrderMatchedQueueHeadOffsetRedisKeyPattern;
    private String pcOrderMatchedQueueEndOffsetRedisKeyPattern;
    private String pcOrderMatchedQueueSizeRedisKeyPattern;

    public String getPcOrderSnapshotRedisKeyPattern() {
        return pcOrderSnapshotRedisKeyPattern;
    }

    public void setPcOrderSnapshotRedisKeyPattern(String pcOrderSnapshotRedisKeyPattern) {
        this.pcOrderSnapshotRedisKeyPattern = pcOrderSnapshotRedisKeyPattern;
    }

    public String getPcOrderSentMqOffsetRedisKeyPattern() {
        return pcOrderSentMqOffsetRedisKeyPattern;
    }

    public void setPcOrderSentMqOffsetRedisKeyPattern(String pcOrderSentMqOffsetRedisKeyPattern) {
        this.pcOrderSentMqOffsetRedisKeyPattern = pcOrderSentMqOffsetRedisKeyPattern;
    }

    public String getPcBookChannelRedisKeyPattern() {
        return pcBookChannelRedisKeyPattern;
    }

    public void setPcBookChannelRedisKeyPattern(String pcBookChannelRedisKeyPattern) {
        this.pcBookChannelRedisKeyPattern = pcBookChannelRedisKeyPattern;
    }

    public String getPcTradeChannelRedisKeyPattern() {
        return pcTradeChannelRedisKeyPattern;
    }

    public void setPcTradeChannelRedisKeyPattern(String pcTradeChannelRedisKeyPattern) {
        this.pcTradeChannelRedisKeyPattern = pcTradeChannelRedisKeyPattern;
    }

    public String getPcOrderMatchedQueueRedisKeyPattern() {
        return pcOrderMatchedQueueRedisKeyPattern;
    }

    public void setPcOrderMatchedQueueRedisKeyPattern(String pcOrderMatchedQueueRedisKeyPattern) {
        this.pcOrderMatchedQueueRedisKeyPattern = pcOrderMatchedQueueRedisKeyPattern;
    }

    public String getPcOrderMatchedQueueHeadOffsetRedisKeyPattern() {
        return pcOrderMatchedQueueHeadOffsetRedisKeyPattern;
    }

    public void setPcOrderMatchedQueueHeadOffsetRedisKeyPattern(String pcOrderMatchedQueueHeadOffsetRedisKeyPattern) {
        this.pcOrderMatchedQueueHeadOffsetRedisKeyPattern = pcOrderMatchedQueueHeadOffsetRedisKeyPattern;
    }

    public String getPcOrderMatchedQueueEndOffsetRedisKeyPattern() {
        return pcOrderMatchedQueueEndOffsetRedisKeyPattern;
    }

    public void setPcOrderMatchedQueueEndOffsetRedisKeyPattern(String pcOrderMatchedQueueEndOffsetRedisKeyPattern) {
        this.pcOrderMatchedQueueEndOffsetRedisKeyPattern = pcOrderMatchedQueueEndOffsetRedisKeyPattern;
    }

    public String getPcOrderMatchedQueueSizeRedisKeyPattern() {
        return pcOrderMatchedQueueSizeRedisKeyPattern;
    }

    public void setPcOrderMatchedQueueSizeRedisKeyPattern(String pcOrderMatchedQueueSizeRedisKeyPattern) {
        this.pcOrderMatchedQueueSizeRedisKeyPattern = pcOrderMatchedQueueSizeRedisKeyPattern;
    }
}
