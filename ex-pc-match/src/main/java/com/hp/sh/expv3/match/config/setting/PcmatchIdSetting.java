/**
 * @author 10086
 * @date 2019/10/11
 */
package com.hp.sh.expv3.match.config.setting;

import com.hp.sh.expv3.match.constant.PcmatchConst;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = PcmatchConst.MODULE_NAME + ".id")
public class PcmatchIdSetting {

    private Integer dataCenterId;
    private Integer serverId;
    private String caller;

    public Integer getDataCenterId() {
        return dataCenterId;
    }

    public Integer getServerId() {
        return serverId;
    }

    public void setDataCenterId(Integer dataCenterId) {
        this.dataCenterId = dataCenterId;
    }

    public void setServerId(Integer serverId) {
        this.serverId = serverId;
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

}