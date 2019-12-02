/**
 * @author zw
 * @date 2019/8/9
 */
package com.hp.sh.expv3.match.config.setting;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "rediskey")
public class RedisKeySetting {

    private String accountChannelRedisKeyPattern;
    private String fundAccountChannelRedisKeyPattern;

    private String pcAccountChannelRedisKeyPattern;
    private String pcOrderChannelRedisKeyPattern;
    private String pcBookChannelRedisKeyPattern;
    private String pcTradeChannelRedisKeyPattern;
    private String pcPosChannelRedisKeyPattern;

    private String orderSnapshotRedisKeyPattern;
    private String orderSentMqOffsetRedisKeyPattern;

    private String pcMarkPriceHistoryPattern;
    private String pcMarkPricePattern;
    private String pcLastPriceHistoryPattern;
    private String pcLastPricePattern;

    private String pcCurrentFundingRatioPattern;
    private String pcNextFundingRatioPattern;

    public String getOrderSnapshotRedisKeyPattern() {
        return orderSnapshotRedisKeyPattern;
    }

    public void setOrderSnapshotRedisKeyPattern(String orderSnapshotRedisKeyPattern) {
        this.orderSnapshotRedisKeyPattern = orderSnapshotRedisKeyPattern;
    }

    public String getOrderSentMqOffsetRedisKeyPattern() {
        return orderSentMqOffsetRedisKeyPattern;
    }

    public void setOrderSentMqOffsetRedisKeyPattern(String orderSentMqOffsetRedisKeyPattern) {
        this.orderSentMqOffsetRedisKeyPattern = orderSentMqOffsetRedisKeyPattern;
    }

    public String getAccountChannelRedisKeyPattern() {
        return accountChannelRedisKeyPattern;
    }

    public void setAccountChannelRedisKeyPattern(String accountChannelRedisKeyPattern) {
        this.accountChannelRedisKeyPattern = accountChannelRedisKeyPattern;
    }

    public String getFundAccountChannelRedisKeyPattern() {
        return fundAccountChannelRedisKeyPattern;
    }

    public void setFundAccountChannelRedisKeyPattern(String fundAccountChannelRedisKeyPattern) {
        this.fundAccountChannelRedisKeyPattern = fundAccountChannelRedisKeyPattern;
    }

    public String getPcOrderChannelRedisKeyPattern() {
        return pcOrderChannelRedisKeyPattern;
    }

    public void setPcOrderChannelRedisKeyPattern(String pcOrderChannelRedisKeyPattern) {
        this.pcOrderChannelRedisKeyPattern = pcOrderChannelRedisKeyPattern;
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

    public String getPcPosChannelRedisKeyPattern() {
        return pcPosChannelRedisKeyPattern;
    }

    public void setPcPosChannelRedisKeyPattern(String pcPosChannelRedisKeyPattern) {
        this.pcPosChannelRedisKeyPattern = pcPosChannelRedisKeyPattern;
    }

    public String getPcAccountChannelRedisKeyPattern() {
        return pcAccountChannelRedisKeyPattern;
    }

    public void setPcAccountChannelRedisKeyPattern(String pcAccountChannelRedisKeyPattern) {
        this.pcAccountChannelRedisKeyPattern = pcAccountChannelRedisKeyPattern;
    }

    public String getPcMarkPriceHistoryPattern() {
        return pcMarkPriceHistoryPattern;
    }

    public void setPcMarkPriceHistoryPattern(String pcMarkPriceHistoryPattern) {
        this.pcMarkPriceHistoryPattern = pcMarkPriceHistoryPattern;
    }

    public String getPcMarkPricePattern() {
        return pcMarkPricePattern;
    }

    public void setPcMarkPricePattern(String pcMarkPricePattern) {
        this.pcMarkPricePattern = pcMarkPricePattern;
    }

    public String getPcLastPricePattern() {
        return pcLastPricePattern;
    }

    public void setPcLastPricePattern(String pcLastPricePattern) {
        this.pcLastPricePattern = pcLastPricePattern;
    }

    public String getPcLastPriceHistoryPattern() {
        return pcLastPriceHistoryPattern;
    }

    public void setPcLastPriceHistoryPattern(String pcLastPriceHistoryPattern) {
        this.pcLastPriceHistoryPattern = pcLastPriceHistoryPattern;
    }

    public String getPcCurrentFundingRatioPattern() {
        return pcCurrentFundingRatioPattern;
    }

    public void setPcCurrentFundingRatioPattern(String pcCurrentFundingRatioPattern) {
        this.pcCurrentFundingRatioPattern = pcCurrentFundingRatioPattern;
    }

    public String getPcNextFundingRatioPattern() {
        return pcNextFundingRatioPattern;
    }

    public void setPcNextFundingRatioPattern(String pcNextFundingRatioPattern) {
        this.pcNextFundingRatioPattern = pcNextFundingRatioPattern;
    }
}
