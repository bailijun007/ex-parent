/**
 * @author 10086
 * @date 2019/10/11
 */
package com.hp.sh.expv3.match.component.id;

import com.hp.sh.expv3.match.config.setting.BbmatchIdSetting;
import com.hp.sh.expv3.match.enums.IdTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class IdWorkerBean {

    @Autowired
    BbmatchIdSetting bbmatchIdSetting;
    @Autowired
    IdBitSetting idSetting;

    @Bean(name = "commonIdWorker")
    public SnowflakeIdWorker buildCommonIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), bbmatchIdSetting.getDataCenterId(), bbmatchIdSetting.getServerId(), IdTypeEnum.COMMON.value);
    }

    @Bean(name = "tradeIdWorker")
    public SnowflakeIdWorker buildTradeIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), bbmatchIdSetting.getDataCenterId(), bbmatchIdSetting.getServerId(), IdTypeEnum.TRADE.value);
    }

    @Bean(name = "matchIdWorker")
    public SnowflakeIdWorker buildMatchIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), bbmatchIdSetting.getDataCenterId(), bbmatchIdSetting.getServerId(), IdTypeEnum.MATCH.value);
    }

}