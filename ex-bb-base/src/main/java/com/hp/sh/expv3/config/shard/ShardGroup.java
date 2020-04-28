package com.hp.sh.expv3.config.shard;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ShardGroup {
	
	public int getMsgSardId(Object key){
		if(key==null){
			return 0;
		}
		int index = Math.abs(key.hashCode()) % ShardConstant.SHARD_NUM;
		return index;
	}
	
	public List<Integer> getShardIdList(){
		List<Integer> shardIdList = new ArrayList<Integer>();
		for(int shardId=0; shardId < ShardConstant.SHARD_NUM; shardId++){
			shardIdList.add(shardId);
		}
		return shardIdList;
	}

}
