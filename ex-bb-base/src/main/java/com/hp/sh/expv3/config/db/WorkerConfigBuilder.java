package com.hp.sh.expv3.config.db;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.collector.entity.BBCollectorAccountRecord;
import com.hp.sh.expv3.bb.module.log.entity.BBAccountLog;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.trade.entity.BBMatchedTrade;
import com.hp.sh.expv3.component.id.config.SequencConfig;
import com.hp.sh.expv3.component.id.config.WorkerConfig;
import com.hp.sh.expv3.enums.IdTypeEnum;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
public class WorkerConfigBuilder {
	
	public static final String TX = "tx";
	private static final int TX_ID = IdTypeEnum.BB_TX_ID.getValue();

	private static final String BB_ACCOUNT_RECORD = BBAccountRecord.class.getName();
	private static final int BB_ACCOUNT_RECORD_ID = IdTypeEnum.BB_ACCOUNT_RECORD_ID.getValue();

	private static final String BB_ORDER = BBOrder.class.getName();
	private static final int BB_ORDER_ID = IdTypeEnum.BB_ORDER_ID.getValue();
	
//	private static final String BB_ORDER_LOG = BBOrderLog.class.getName();
//	private static final int BB_ORDER_LOG_ID = IdTypeEnum.BB_ORDER_LOG_ID.getValue();

	private static final String BB_ORDER_TRADE = BBOrderTrade.class.getName();
	private static final int BB_ORDER_TRADE_ID = IdTypeEnum.BB_ORDER_TRADE_ID.getValue();

//	private static final String BB_ACTIVE_ORDER = BBActiveOrder.class.getName();
//	private static final int BB_ACTIVE_ORDER_ID = IdTypeEnum.BB_ACTIVE_ORDER_ID.getValue();

	private static final String BB_MATCHED_TRADE = BBMatchedTrade.class.getName();
	private static final int BB_MATCHED_TRADE_ID = IdTypeEnum.BB_MATCHED_TRADE_ID.getValue();

	private static final String BB_COLLECTOR_ACCOUNT_RECORD = BBCollectorAccountRecord.class.getName();
	private static final int BB_COLLECTOR_ACCOUNT_RECORD_ID = IdTypeEnum.BB_COLLECTOR_ACCOUNT_RECORD_ID.getValue();

	private static final String BB_ACCOUNT_LOG = BBAccountLog.class.getName();
	private static final int BB_ACCOUNT_LOG_ID = IdTypeEnum.BB_ACCOUNT_LOG_ID.getValue();

	private static final String BB_MESSAGE_EXT = BBMessageExt.class.getName();
	private static final int BB_MESSAGE_EXT_ID = IdTypeEnum.BB_MESSAGE_EXT_ID.getValue();
	
	
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
		this.set(workerConfig, BB_ACCOUNT_LOG_ID, BB_ACCOUNT_LOG);
		this.set(workerConfig, BB_ACCOUNT_RECORD_ID, BB_ACCOUNT_RECORD);
		this.set(workerConfig, BB_ORDER_ID, BB_ORDER);
//		this.set(workerConfig, BB_ORDER_LOG_ID, BB_ORDER_LOG);
		this.set(workerConfig, BB_ORDER_TRADE_ID, BB_ORDER_TRADE);
//		this.set(workerConfig, BB_ACTIVE_ORDER_ID, BB_ACTIVE_ORDER);
		this.set(workerConfig, BB_MATCHED_TRADE_ID, BB_MATCHED_TRADE);
		this.set(workerConfig, BB_COLLECTOR_ACCOUNT_RECORD_ID, BB_COLLECTOR_ACCOUNT_RECORD);
		this.set(workerConfig, BB_MESSAGE_EXT_ID, BB_MESSAGE_EXT);
	}
	
	private void set(WorkerConfig workerConfig, int seqId, String tableUUID){
		workerConfig.getSequencConfigs().add(new SequencConfig(seqId));
		workerConfig.getEntitySeqPairs().add(Pair.of(tableUUID, seqId));
	}
	
}
