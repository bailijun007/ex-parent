package com.hp.sh.expv3.config.shard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ShardGroup {

	public final Map<Long,Long> userThreadShardMap = new HashMap<>();
	
	public final Map<Long,Long> userDbShardMap = new HashMap<>();

	@Value("${user.vip.thread.userIds:}")
	private String vipThreadUserIds;
	
	@Value("${user.vip.db.userIds:}")
	private String vipDBUserIds;
	
	public ShardGroup() {
	}

	public void addDbShard(Long userId, Long shardId){
		userDbShardMap.put(userId, shardId);
	}

	public void addUserShard(Long userId, Long shardId){
		userThreadShardMap.put(userId, shardId);
	}
	
	public Long getMsgSardId(Object key){
		if(key==null){
			return 0L;
		}
		if(this.userThreadShardMap.containsKey(key)){
			return userThreadShardMap.get(key);
		}
		int index = Math.abs(key.hashCode()) % ShardConstant.SHARD_NUM;
		return (long) index;
	}
	
	public List<Long> getShardIdList(){
		List<Long> shardIdList = new ArrayList<Long>();
		for(Long shardId=0L; shardId < ShardConstant.SHARD_NUM; shardId++){
			shardIdList.add(shardId);
		}
		shardIdList.addAll(this.userThreadShardMap.values());
		return shardIdList;
	}

	@PostConstruct
	public void initVipShard(){
		if(StringUtils.isNotBlank(vipThreadUserIds)){
			String[] tUserIds = this.vipThreadUserIds.split(",");
			for(String uid : tUserIds){
				Long userId = Long.parseLong(uid);
				Long shardId = Math.abs(userId)*-1;
				this.addUserShard(userId, shardId);
			}
		}
		
		if(StringUtils.isNotBlank(vipDBUserIds)){
			String[] dbUserIds = this.vipDBUserIds.split(",");
			for(String uid : dbUserIds){
				Long userId = Long.parseLong(uid);
				Long shardId = Math.abs(userId)*-1;
				this.addDbShard(userId, shardId);
			}
		}
		
	}
	
}
