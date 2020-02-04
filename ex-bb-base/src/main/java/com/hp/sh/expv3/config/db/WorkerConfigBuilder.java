package com.hp.sh.expv3.config.db;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.module.account.entity.BBAccount;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.order.entity.BBActiveOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderLog;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.position.entity.BBLiqRecord;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.module.symbol.entity.BBAccountSymbol;
import com.hp.sh.expv3.bb.msg.BBAccountLog;
import com.hp.sh.expv3.component.id.config.SequencConfig;
import com.hp.sh.expv3.component.id.config.WorkerConfig;

@Component
public class WorkerConfigBuilder {
	
	public static final String TX = "tx";
	private static final int TX_ID = 0;
	
	private static final String PC_ACCOUNT = BBAccount.class.getName();
	private static final int PC_ACCOUNT_ID = 1;

	private static final String PC_ACCOUNT_LOG = BBAccountLog.class.getName();
	private static final int PC_ACCOUNT_LOG_ID = 2;

	private static final String PC_ACCOUNT_RECORD = BBAccountRecord.class.getName();
	private static final int PC_ACCOUNT_RECORD_ID = 3;

	private static final String PC_ACCOUNT_SYMBOL = BBAccountSymbol.class.getName();
	private static final int PC_ACCOUNT_SYMBOL_ID = 4;

	private static final String PC_LIQ_RECORD = BBLiqRecord.class.getName();
	private static final int PC_LIQ_RECORD_ID = 5;

	private static final String PC_ORDER = BBOrder.class.getName();
	private static final int PC_ORDER_ID = 6;
	
	private static final String PC_ORDER_LOG = BBOrderLog.class.getName();
	private static final int PC_ORDER_LOG_ID = 7;

	private static final String PC_ORDER_TRADE = BBOrderTrade.class.getName();
	private static final int PC_ORDER_TRADE_ID = 8;

	private static final String PC_POSITION = BBPosition.class.getName();
	private static final int PC_POSITION_ID = 9;
	
	private static final String PC_ACTIVE_ORDER = BBActiveOrder.class.getName();
	private static final int PC_ACTIVE_ORDER_ID = 10;
	
	
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
		this.set(workerConfig, TX_ID, TX);
		this.set(workerConfig, PC_ACCOUNT_ID, PC_ACCOUNT);
		this.set(workerConfig, PC_ACCOUNT_LOG_ID, PC_ACCOUNT_LOG);
		this.set(workerConfig, PC_ACCOUNT_RECORD_ID, PC_ACCOUNT_RECORD);
		this.set(workerConfig, PC_ACCOUNT_SYMBOL_ID, PC_ACCOUNT_SYMBOL);
		this.set(workerConfig, PC_LIQ_RECORD_ID, PC_LIQ_RECORD);
		this.set(workerConfig, PC_ORDER_ID, PC_ORDER);
		this.set(workerConfig, PC_ORDER_LOG_ID, PC_ORDER_LOG);
		this.set(workerConfig, PC_ORDER_TRADE_ID, PC_ORDER_TRADE);
		this.set(workerConfig, PC_POSITION_ID, PC_POSITION);
		this.set(workerConfig, PC_ACTIVE_ORDER_ID, PC_ACTIVE_ORDER);
	}
	
	private void set(WorkerConfig workerConfig, int seqId, String tableUUID){
		workerConfig.getSequencConfigs().add(new SequencConfig(seqId));
		workerConfig.getEntitySeqPairs().add(Pair.of(tableUUID, seqId));
	}
	
}
