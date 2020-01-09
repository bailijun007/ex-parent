/**
 * @author zw
 * @date 2019/7/24
 */
package com.hp.sh.expv3.match.component.id;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "id")
public class IdBitSetting {

    private int dataCenterBits;
    private int serverBits;
    private int idTypeBits;
    private int sequenceBits;

    public int getDataCenterBits() {
        return dataCenterBits;
    }

    public void setDataCenterBits(int dataCenterBits) {
        this.dataCenterBits = dataCenterBits;
    }

    public int getServerBits() {
        return serverBits;
    }

    public void setServerBits(int serverBits) {
        this.serverBits = serverBits;
    }

    public int getIdTypeBits() {
        return idTypeBits;
    }

    public void setIdTypeBits(int idTypeBits) {
        this.idTypeBits = idTypeBits;
    }

    public int getSequenceBits() {
        return sequenceBits;
    }

    public void setSequenceBits(int sequenceBits) {
        this.sequenceBits = sequenceBits;
    }
}
