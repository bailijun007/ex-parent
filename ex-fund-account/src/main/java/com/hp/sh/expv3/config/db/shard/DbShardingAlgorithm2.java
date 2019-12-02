package com.hp.sh.expv3.config.db.shard;

import java.util.Collection;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

public class DbShardingAlgorithm2 implements ComplexKeysShardingAlgorithm<Long>{

	public DbShardingAlgorithm2() {
		super();
	}

	@Override
	public Collection<String> doSharding(Collection<String> availableTargetNames, ComplexKeysShardingValue<Long> shardingValue) {
		
		return availableTargetNames;
	}

}
