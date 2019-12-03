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

    @Bean(name = "accountIdWorker")
    public SnowflakeIdWorker buildAccountIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.ACCOUNT.value);
    }

    @Bean(name = "transactionIdWorker")
    public SnowflakeIdWorker buildTransactionIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.TRANSACTION.value);
    }

    @Bean(name = "orderIdWorker")
    public SnowflakeIdWorker buildOrderIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.ORDER.value);
    }

    @Bean(name = "tradeIdWorker")
    public SnowflakeIdWorker buildTradeIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.TRADE.value);
    }

    @Bean(name = "depositIdWorker")
    public SnowflakeIdWorker buildDepositIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.DEPOSIT.value);
    }

    @Bean(name = "withdrawIdWorker")
    public SnowflakeIdWorker buildWithdrawIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.WITHDRAW.value);
    }

    @Bean(name = "positionIdWorker")
    public SnowflakeIdWorker buildPositionIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.POSITION.value);
    }

    @Bean(name = "matchIdWorker")
    public SnowflakeIdWorker buildMatchIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.MATCH.value);
    }

    @Bean(name = "transferIdWorker")
    public SnowflakeIdWorker buildTransferIdWorker() {
        return IdUtil.newIdWorker(idSetting.getDataCenterBits(), idSetting.getServerBits(), idSetting.getIdTypeBits(), idSetting.getSequenceBits(), pcmatchIdSetting.getDataCenterId(), pcmatchIdSetting.getServerId(), IdTypeEnum.TRANSFER.value);
    }


}