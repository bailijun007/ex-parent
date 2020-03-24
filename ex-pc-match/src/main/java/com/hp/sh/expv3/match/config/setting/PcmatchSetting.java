/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.PcmatchConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = PcmatchConst.MODULE_NAME)
public class PcmatchSetting {

    private Integer matchGroupId;
    private int saveTradeBatchSize;

    public int getSaveTradeBatchSize() {
        return saveTradeBatchSize;
    }

    public void setSaveTradeBatchSize(int saveTradeBatchSize) {
        this.saveTradeBatchSize = saveTradeBatchSize;
    }

    public Integer getMatchGroupId() {
        return matchGroupId;
    }

    public void setMatchGroupId(Integer matchGroupId) {
        this.matchGroupId = matchGroupId;
    }


}