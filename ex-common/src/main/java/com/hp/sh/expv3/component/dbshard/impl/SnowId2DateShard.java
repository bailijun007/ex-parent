package com.hp.sh.expv3.component.dbshard.impl;

import com.hp.sh.expv3.component.context.IdGeneratorContext;
import com.hp.sh.expv3.component.dbshard.DateShardUtils;
import com.hp.sh.expv3.component.dbshard.IdDateShard;

public class SnowId2DateShard implements IdDateShard{

	public String getDateShardById(Object id){
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
