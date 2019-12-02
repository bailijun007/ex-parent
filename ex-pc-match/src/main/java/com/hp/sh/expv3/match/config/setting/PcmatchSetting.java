/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.PcmatchConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
@ConfigurationProperties(prefix = PcmatchConst.MODULE_NAME)
public class PcmatchSetting {

    private Set<String> supportAssetSymbol;

    public Set<String> getSupportAssetSymbol() {
        return supportAssetSymbol;
    }

    public void setSupportAssetSymbol(Set<String> supportAssetSymbol) {
        this.supportAssetSymbol = supportAssetSymbol;
    }

}