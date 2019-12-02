package com.hp.sh.expv3.config.db.shard;

import java.util.Collection;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

public class DbHintShardingAlgorithm implements HintShardingAlgorithm{

	@Override
	public Collection doSharding(Collection availableTargetNames, HintShardingValue shardingValue) {
		
		return availableTargetNames;
	}

}
