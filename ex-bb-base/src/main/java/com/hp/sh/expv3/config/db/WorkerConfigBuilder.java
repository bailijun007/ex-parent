package com.hp.sh.expv3.config.db;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.module.account.entity.BBAccount;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.collector.entity.BBAccountLog;
import com.hp.sh.expv3.bb.module.order.entity.BBActiveOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderLog;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.component.id.config.SequencConfig;
import com.hp.sh.expv3.component.id.config.WorkerConfig;

@Component
public class WorkerConfigBuilder {
	
	public static final String TX = "tx";
	private static final int TX_ID = 0;
	
	private static final String BB_ACCOUNT = BBAccount.class.getName();
	private static final int BB_ACCOUNT_ID = 1;

	private static final String BB_ACCOUNT_LOG = BBAccountLog.class.getName();
	private static final int BB_ACCOUNT_LOG_ID = 2;

	private static final String BB_ACCOUNT_RECORD = BBAccountRecord.class.getName();
	private static final int BB_ACCOUNT_RECORD_ID = 3;

	private static final String BB_ORDER = BBOrder.class.getName();
	private static final int BB_ORDER_ID = 6;
	
	private static final String BB_ORDER_LOG = BBOrderLog.class.getName();
	private static final int BB_ORDER_LOG_ID = 7;

	private static final String BB_ORDER_TRADE = BBOrderTrade.class.getName();
	private static final int BB_ORDER_TRADE_ID = 8;

	private static final String BB_ACTIVE_ORDER = BBActiveOrder.class.getName();
	private static final int BB_ACTIVE_ORDER_ID = 10;

	private static final String BB_MATCHED_TRADE = BBMatchedTrade.class.getName();
	private static final int BB_MATCHED_TRADE_ID = 11;
	
	
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
		this.set(workerConfig, BB_ACCOUNT_ID, BB_ACCOUNT);
		this.set(workerConfig, BB_ACCOUNT_LOG_ID, BB_ACCOUNT_LOG);
		this.set(workerConfig, BB_ACCOUNT_RECORD_ID, BB_ACCOUNT_RECORD);
		this.set(workerConfig, BB_ORDER_ID, BB_ORDER);
		this.set(workerConfig, BB_ORDER_LOG_ID, BB_ORDER_LOG);
		this.set(workerConfig, BB_ORDER_TRADE_ID, BB_ORDER_TRADE);
		this.set(workerConfig, BB_ACTIVE_ORDER_ID, BB_ACTIVE_ORDER);
		this.set(workerConfig, BB_MATCHED_TRADE_ID, BB_MATCHED_TRADE);
	}
	
	private void set(WorkerConfig workerConfig, int seqId, String tableUUID){
		workerConfig.getSequencConfigs().add(new SequencConfig(seqId));
		workerConfig.getEntitySeqPairs().add(Pair.of(tableUUID, seqId));
	}
	
}
