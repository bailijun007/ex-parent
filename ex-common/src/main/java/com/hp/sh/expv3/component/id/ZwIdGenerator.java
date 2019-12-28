package com.hp.sh.expv3.component.id;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.gitee.hupadev.commons.id.IdGenerator;
import com.hp.sh.expv3.component.id.config.SequencConfig;
import com.hp.sh.expv3.component.id.config.WorkerConfig;
import com.hp.sh.expv3.component.id.utils.GeneratorName;
import com.hp.sh.expv3.component.id.utils.IdUtil;
import com.hp.sh.expv3.component.id.utils.SnowflakeIdWorker;

public class ZwIdGenerator implements IdGenerator{
	
	private final Map<Integer, SnowflakeIdWorker> idWorkerMap = new HashMap<Integer, SnowflakeIdWorker>();
	
	private final Map<String, Integer> sequenceNameMap = new HashMap<String, Integer>();

	public ZwIdGenerator(WorkerConfig workerConfig) {
		this(workerConfig.getDataCenterId(), workerConfig.getServerId(), workerConfig.getSequencConfigs(), workerConfig.getSeqIdPairs());
	}

	public ZwIdGenerator(int dataCenterId, int serverId, List<SequencConfig> idSettings, List<Pair<String,Integer>> seqIdPairs) {
		for(SequencConfig sequencConfig : idSettings){
			SnowflakeIdWorker idworker = IdUtil.newIdWorker(sequencConfig.getDataCenterBits(), sequencConfig.getServerBits(), sequencConfig.getIdTypeBits(), sequencConfig.getSequenceBits(), dataCenterId, serverId, 0);
			idWorkerMap.put(sequencConfig.getSequencId(), idworker);
		}
		for(Pair<String,Integer> nameIdPair: seqIdPairs){
			sequenceNameMap.put(nameIdPair.getKey(), nameIdPair.getValue());
		}
	}

	@Override
	public Long nextId(String tableUUID) {
		Integer seqId = this.sequenceNameMap.get(tableUUID);
		SnowflakeIdWorker idworker = idWorkerMap.get(seqId);
		return idworker.nextId();
	}

	@Override
	public String getName() {
		return GeneratorName.SNOWFLAKE;
	}
	
	static class IdWorkerPair{
	    private transient SnowflakeIdWorker snowflakeIdWorker;
	    
	    
	}

}
