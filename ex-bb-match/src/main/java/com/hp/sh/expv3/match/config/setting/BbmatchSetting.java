/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.BbmatchConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = BbmatchConst.MODULE_NAME)
public class BbmatchSetting {

    private Integer matchGroupId;
    private Integer sendMatchBatchEnable;
    private Integer tradeBatchSize;

    public Integer getTradeBatchSize() {
        return tradeBatchSize;
    }

    public void setTradeBatchSize(Integer tradeBatchSize) {
        this.tradeBatchSize = tradeBatchSize;
    }

    public Integer getSendMatchBatchEnable() {
        return sendMatchBatchEnable;
    }

    public void setSendMatchBatchEnable(Integer sendMatchBatchEnable) {
        this.sendMatchBatchEnable = sendMatchBatchEnable;
    }

    public Integer getMatchGroupId() {
        return matchGroupId;
    }

    public void setMatchGroupId(Integer matchGroupId) {
        this.matchGroupId = matchGroupId;
    }


}