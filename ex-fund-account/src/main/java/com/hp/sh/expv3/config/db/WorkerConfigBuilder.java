package com.hp.sh.expv3.config.db;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.component.id.config.SequencConfig;
import com.hp.sh.expv3.component.id.config.WorkerConfig;
import com.hp.sh.expv3.fund.cash.entity.DepositAddr;
import com.hp.sh.expv3.fund.cash.entity.DepositRecord;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalAddr;
import com.hp.sh.expv3.fund.cash.entity.WithdrawalRecord;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;
import com.hp.sh.expv3.fund.wallet.entity.FundAccount;
import com.hp.sh.expv3.fund.wallet.entity.FundAccountRecord;

@Component
public class WorkerConfigBuilder {
	
	public static final String 	USER  = "user";
	private static final int 	USER_ID  = 0;

	private static final String DEPOSIT_ADDR  = DepositAddr.class.getName();
	private static final int 	DEPOSIT_ADDR_ID  = 1;

	private static final String DEPOSIT_RECORD  = DepositRecord.class.getName();
	private static final int 	DEPOSIT_RECORD_ID  = 2;

	private static final String FUND_ACCOUNT  = FundAccount.class.getName();
	private static final int 	FUND_ACCOUNT_ID  = 3;

	private static final String FUND_ACCOUNT_RECORD  = FundAccountRecord.class.getName();
	private static final int 	FUND_ACCOUNT_RECORD_ID  = 4;

	private static final String FUND_TRANSFER  = FundTransfer.class.getName();
	private static final int 	FUND_TRANSFER_ID  = 5;

	private static final String WITHDRAWAL_ADDR  = WithdrawalAddr.class.getName();
	private static final int 	WITHDRAWAL_ADDR_ID  = 6;

	private static final String WITHDRAWAL_RECORD  = WithdrawalRecord.class.getName();
	private static final int 	WITHDRAWAL_RECORD_ID  = 7;
	
	@Value("${id.generator.dataCenterId}")
	private int dataCenterId;
	
	@Value("${id.generator.serverId}")
	private int serverId;
	
	public WorkerConfig getWorkerConfig(){
		WorkerConfig workerConfig = new WorkerConfig();
		workerConfig.setDataCenterId(dataCenterId);
		workerConfig.setServerId(serverId);
		workerConfig.setSequencConfigs(new ArrayList<SequencConfig>());
		workerConfig.setEntitySeqPairs(new ArrayList<Pair<String, Integer>>());
		this.set(workerConfig);
		return workerConfig;
	}
	
	private void set(WorkerConfig workerConfig){
		this.set(workerConfig, USER_ID, USER);
		this.set(workerConfig, DEPOSIT_ADDR_ID, DEPOSIT_ADDR);
		this.set(workerConfig, DEPOSIT_RECORD_ID, DEPOSIT_RECORD);
		this.set(workerConfig, FUND_ACCOUNT_ID, FUND_ACCOUNT);
		this.set(workerConfig, FUND_ACCOUNT_RECORD_ID, FUND_ACCOUNT_RECORD);
		this.set(workerConfig, FUND_TRANSFER_ID, FUND_TRANSFER);
		this.set(workerConfig, WITHDRAWAL_ADDR_ID, WITHDRAWAL_ADDR);
		this.set(workerConfig, WITHDRAWAL_RECORD_ID, WITHDRAWAL_RECORD);
	}
	
	private void set(WorkerConfig workerConfig, int seqId, String tableUUID){
		workerConfig.getSequencConfigs().add(new SequencConfig(seqId));
		workerConfig.getEntitySeqPairs().add(Pair.of(tableUUID, seqId));
	}
	
}
