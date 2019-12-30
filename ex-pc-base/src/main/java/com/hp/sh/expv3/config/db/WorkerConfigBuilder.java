package com.hp.sh.expv3.config.db;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.component.id.config.SequencConfig;
import com.hp.sh.expv3.component.id.config.WorkerConfig;
import com.hp.sh.expv3.pc.module.account.entity.PcAccount;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecord;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.position.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.symbol.entity.PcAccountSymbol;
import com.hp.sh.expv3.pc.msg.PcAccountLog;

@Component
public class WorkerConfigBuilder {
	
	private static final String PC_ACCOUNT = PcAccount.class.getName();
	private static final int PC_ACCOUNT_ID = 1;

	private static final String PC_ACCOUNT_LOG = PcAccountLog.class.getName();
	private static final int PC_ACCOUNT_LOG_ID = 2;

	private static final String PC_ACCOUNT_RECORD = PcAccountRecord.class.getName();
	private static final int PC_ACCOUNT_RECORD_ID = 3;

	private static final String PC_ACCOUNT_SYMBOL = PcAccountSymbol.class.getName();
	private static final int PC_ACCOUNT_SYMBOL_ID = 4;

	private static final String PC_LIQ_RECORD = PcLiqRecord.class.getName();
	private static final int PC_LIQ_RECORD_ID = 5;

	private static final String PC_ORDER = PcOrder.class.getName();
	private static final int PC_ORDER_ID = 6;

	private static final String PC_ORDER_TRADE = PcOrderTrade.class.getName();
	private static final int PC_ORDER_TRADE_ID = 7;

	private static final String PC_POSITION = PcPosition.class.getName();
	private static final int PC_POSITION_ID = 8;
	
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
		this.set(workerConfig, PC_ACCOUNT_ID, PC_ACCOUNT);
		this.set(workerConfig, PC_ACCOUNT_LOG_ID, PC_ACCOUNT_LOG);
		this.set(workerConfig, PC_ACCOUNT_RECORD_ID, PC_ACCOUNT_RECORD);
		this.set(workerConfig, PC_ACCOUNT_SYMBOL_ID, PC_ACCOUNT_SYMBOL);
		this.set(workerConfig, PC_LIQ_RECORD_ID, PC_LIQ_RECORD);
		this.set(workerConfig, PC_ORDER_ID, PC_ORDER);
		this.set(workerConfig, PC_ORDER_TRADE_ID, PC_ORDER_TRADE);
		this.set(workerConfig, PC_POSITION_ID, PC_POSITION);
	}
	
	private void set(WorkerConfig workerConfig, int seqId, String tableUUID){
		workerConfig.getSequencConfigs().add(new SequencConfig(seqId));
		workerConfig.getEntitySeqPairs().add(Pair.of(tableUUID, seqId));
	}
	
}