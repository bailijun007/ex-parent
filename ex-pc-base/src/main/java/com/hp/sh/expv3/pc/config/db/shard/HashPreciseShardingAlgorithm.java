/**
 * 
 */
package com.hp.sh.expv3.pc.config.db.shard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

/**
 * @author wangjg
 */
public class HashPreciseShardingAlgorithm implements PreciseShardingAlgorithm<Comparable<?>> {
	
	public HashPreciseShardingAlgorithm() {
	}

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Comparable<?>> shardingValue) {
		List<String> list = new ArrayList<String>(availableTargetNames);
		int index = this.getDsIndex(availableTargetNames.size(), shardingValue.getValue());
		String shardName = list.get(index);
		return shardName;
	}
	
	private Integer getDsIndex(int size, Object value){
		if(value==null){
			return 0;
		}
		int i = value.hashCode() % size;
		return i;
	}
	
	public static void main(String[] args) {
        // 自定义的分片算法实现
		TableRuleConfiguration msgTableRuleConfig = new TableRuleConfiguration("gq_user_message", "ds${0.."+2+"}.gq_user_message");
		StandardShardingStrategyConfiguration standardStrategy = new StandardShardingStrategyConfiguration("user_id", new HashPreciseShardingAlgorithm());
		msgTableRuleConfig.setDatabaseShardingStrategyConfig(standardStrategy);
	}

}