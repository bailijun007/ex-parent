package com.hp.sh.expv3.config.shard;

import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.executor.orderly.KeyGroup;

@Component
public class ShardGroup implements KeyGroup{
	
	public int getMsgSardId(Object key){
		return getGroupId(key);
	}

	@Override
	public int getGroupId(Object key) {
		if(key==null){
			return 0;
		}
		int index = Math.abs(key.hashCode()) % ShardConstant.SHARD_NUM;
		return index;
	}

}
