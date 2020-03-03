/**
 * @author 10086
 * @date 2019/10/11
 */
package com.hp.sh.expv3.match.component.id;


import com.hp.sh.expv3.match.config.setting.PcmatchIdSetting;
import com.hp.sh.expv3.match.enums.IdTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class IdWorkerBean {

    @Autowired
    PcmatchIdSetting pcmatchIdSetting;
    @Autowired
    IdBitSetting idSetting;

    @Bean(name = "commonIdWorker")
    public SnowflakeIdWorker buildCommonIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.COMMON.value);
    }

    @Bean(name = "pcTradeIdWorker")
    public SnowflakeIdWorker buildTradeIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.PC_TRADE.value);
    }

    @Bean(name = "pcMatchIdWorker")
    public SnowflakeIdWorker buildMatchIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.PC_MATCH.value);
    }

}