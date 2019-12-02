/**
 * @author zw
 * @date 2019/8/9
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.PcmatchConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = PcmatchConst.MODULE_NAME + "rmq")
public class RocketMqSetting {

    private String nameSrvAddr;
    private String nameSpace;

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSrvAddr() {
        return nameSrvAddr;
    }

    public void setNameSrvAddr(String nameSrvAddr) {
        this.nameSrvAddr = nameSrvAddr;
    }
}
