package com.hp.sh.expv3.component.id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gitee.hupadev.commons.id.IdGenerator;
import com.hp.sh.expv3.component.id.config.SequencConfig;
import com.hp.sh.expv3.component.id.config.WorkerConfig;
import com.hp.sh.expv3.component.id.utils.GeneratorName;
import com.hp.sh.expv3.component.id.utils.IdUtil;
import com.hp.sh.expv3.component.id.utils.SnowflakeIdWorker;

public class ZwIdGenerator implements IdGenerator{
	private final Logger logger = LoggerFactory.getLogger(ZwIdGenerator.class);
	
	private final Map<Integer, SnowflakeIdWorker> idWorkerMap = new HashMap<Integer, SnowflakeIdWorker>();
	
	private final Map<String, Integer> entitySequenceMap = new HashMap<String, Integer>();

	public ZwIdGenerator(WorkerConfig workerConfig) {
		this(workerConfig.getDataCenterId(), workerConfig.getServerId(), workerConfig.getSequencConfigs(), workerConfig.getEntitySeqPairs());
	}

	public ZwIdGenerator(int dataCenterId, int serverId, List<SequencConfig> idSettings, List<Pair<String,Integer>> entityPairs) {
		for(SequencConfig sequencConfig : idSettings){
			SnowflakeIdWorker idworker = IdUtil.newIdWorker(sequencConfig.getDataCenterBits(), sequencConfig.getServerBits(), sequencConfig.getIdTypeBits(), sequencConfig.getSequenceBits(), dataCenterId, serverId, sequencConfig.getSequencId());
			idWorkerMap.put(sequencConfig.getSequencId(), idworker);
		}
		for(Pair<String,Integer> nameIdPair: entityPairs){
			entitySequenceMap.put(nameIdPair.getKey(), nameIdPair.getValue());
		}
	}

	@Override
	public Long nextId(String entityId) {
		Integer seqId = this.entitySequenceMap.get(entityId);
		SnowflakeIdWorker idworker = idWorkerMap.get(seqId);
		if(idworker==null){
			logger.error("idworker is null, entityId={}, seqId={}", entityId, seqId);
		}
		return idworker.nextId();
	}

	@Override
	public String getName() {
		return GeneratorName.SNOWFLAKE;
	}
	
	public long getTimeStamp(String className, Long id){
		String entityId = className;
		Integer seqId = this.entitySequenceMap.get(entityId);
		SnowflakeIdWorker idworker = idWorkerMap.get(seqId);
		long time = idworker.getTime(id);
		return time;
	}

}
