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
    private Integer matchSaveBatchSize;

    public Integer getMatchSaveBatchSize() {
        return matchSaveBatchSize;
    }

    public void setMatchSaveBatchSize(Integer matchSaveBatchSize) {
        this.matchSaveBatchSize = matchSaveBatchSize;
    }

    public Integer getMatchGroupId() {
        return matchGroupId;
    }

    public void setMatchGroupId(Integer matchGroupId) {
        this.matchGroupId = matchGroupId;
    }


}