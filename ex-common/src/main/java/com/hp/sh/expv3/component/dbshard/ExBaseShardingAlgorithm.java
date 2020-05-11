package com.hp.sh.expv3.component.dbshard;

import com.hp.sh.expv3.component.context.IdGeneratorContext;

public class ExBaseShardingAlgorithm {

	protected String getDateShardById(Object id){
		if(id instanceof Long){
			Long time = IdGeneratorContext.getSnowIdTime((Long)id);
			String shardTime = DateShardUtils.getDateShard(time);
			return shardTime;
		}else if(id instanceof String){
			String month = ((String)id).substring(0, 6);
			return month;
		}else{
			throw new RuntimeException();
		}
	}

	
}
