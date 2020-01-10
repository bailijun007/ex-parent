/**
 * @author zw
 * @date 2019/8/9
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.BbmatchConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = BbmatchConst.MODULE_NAME + ".rediskey")
public class BbmatchRedisKeySetting {

    private String bbBookChannelRedisKeyPattern;
    private String bbTradeChannelRedisKeyPattern;
    private String bbOrderSnapshotRedisKeyPattern;
    private String bbOrderSentMqOffsetRedisKeyPattern;

    private String bbOrderMatchedQueueRedisKeyPattern;
    private String bbOrderMatchedQueueHeadOffsetRedisKeyPattern;
    private String bbOrderMatchedQueueEndOffsetRedisKeyPattern;
    private String bbOrderMatchedQueueSizeRedisKeyPattern;

    private String bbPattern;

    public String getBbBookChannelRedisKeyPattern() {
        return bbBookChannelRedisKeyPattern;
    }

    public void setBbBookChannelRedisKeyPattern(String bbBookChannelRedisKeyPattern) {
        this.bbBookChannelRedisKeyPattern = bbBookChannelRedisKeyPattern;
    }

    public String getBbTradeChannelRedisKeyPattern() {
        return bbTradeChannelRedisKeyPattern;
    }

    public void setBbTradeChannelRedisKeyPattern(String bbTradeChannelRedisKeyPattern) {
        this.bbTradeChannelRedisKeyPattern = bbTradeChannelRedisKeyPattern;
    }

    public String getBbOrderSnapshotRedisKeyPattern() {
        return bbOrderSnapshotRedisKeyPattern;
    }

    public void setBbOrderSnapshotRedisKeyPattern(String bbOrderSnapshotRedisKeyPattern) {
        this.bbOrderSnapshotRedisKeyPattern = bbOrderSnapshotRedisKeyPattern;
    }

    public String getBbOrderSentMqOffsetRedisKeyPattern() {
        return bbOrderSentMqOffsetRedisKeyPattern;
    }

    public void setBbOrderSentMqOffsetRedisKeyPattern(String bbOrderSentMqOffsetRedisKeyPattern) {
        this.bbOrderSentMqOffsetRedisKeyPattern = bbOrderSentMqOffsetRedisKeyPattern;
    }

    public String getBbOrderMatchedQueueRedisKeyPattern() {
        return bbOrderMatchedQueueRedisKeyPattern;
    }

    public void setBbOrderMatchedQueueRedisKeyPattern(String bbOrderMatchedQueueRedisKeyPattern) {
        this.bbOrderMatchedQueueRedisKeyPattern = bbOrderMatchedQueueRedisKeyPattern;
    }

    public String getBbOrderMatchedQueueHeadOffsetRedisKeyPattern() {
        return bbOrderMatchedQueueHeadOffsetRedisKeyPattern;
    }

    public void setBbOrderMatchedQueueHeadOffsetRedisKeyPattern(String bbOrderMatchedQueueHeadOffsetRedisKeyPattern) {
        this.bbOrderMatchedQueueHeadOffsetRedisKeyPattern = bbOrderMatchedQueueHeadOffsetRedisKeyPattern;
    }

    public String getBbOrderMatchedQueueEndOffsetRedisKeyPattern() {
        return bbOrderMatchedQueueEndOffsetRedisKeyPattern;
    }

    public void setBbOrderMatchedQueueEndOffsetRedisKeyPattern(String bbOrderMatchedQueueEndOffsetRedisKeyPattern) {
        this.bbOrderMatchedQueueEndOffsetRedisKeyPattern = bbOrderMatchedQueueEndOffsetRedisKeyPattern;
    }

    public String getBbOrderMatchedQueueSizeRedisKeyPattern() {
        return bbOrderMatchedQueueSizeRedisKeyPattern;
    }

    public void setBbOrderMatchedQueueSizeRedisKeyPattern(String bbOrderMatchedQueueSizeRedisKeyPattern) {
        this.bbOrderMatchedQueueSizeRedisKeyPattern = bbOrderMatchedQueueSizeRedisKeyPattern;
    }

    public String getBbPattern() {
        return bbPattern;
    }

    public void setBbPattern(String bbPattern) {
        this.bbPattern = bbPattern;
    }
}
