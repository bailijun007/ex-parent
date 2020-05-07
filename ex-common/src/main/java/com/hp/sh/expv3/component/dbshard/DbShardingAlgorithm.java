/**
 * 
 */
package com.hp.sh.expv3.component.dbshard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wangjg
 */
public class DbShardingAlgorithm implements PreciseShardingAlgorithm<Comparable<?>> {
	private static final Logger logger = LoggerFactory.getLogger(DbShardingAlgorithm.class);
	
	public DbShardingAlgorithm() {
	}

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Comparable<?>> shardingValue) {
		logger.debug("availableTargetNames:{}", availableTargetNames);
		logger.debug("shardingValue:{}", shardingValue);
		
		List<String> dsNameList = new ArrayList<String>(availableTargetNames);
		int index = this.getDsIndex(availableTargetNames.size(), shardingValue.getValue());
		String dataSourceName = dsNameList.get(index);
		return dataSourceName;
	}
	
	private Integer getDsIndex(int size, Object value){
		if(value==null){
			return 0;
		}
		int hc = value.hashCode();
		hc = Math.abs(hc);
		int i = hc % size;
		return i;
	}
	
	public static void main(String[] args) {
        // 自定义的分片算法实现
		TableRuleConfiguration msgTableRuleConfig = new TableRuleConfiguration("gq_user_message", "ds${0.."+2+"}.gq_user_message");
		StandardShardingStrategyConfiguration standardStrategy = new StandardShardingStrategyConfiguration("user_id", new DbShardingAlgorithm());
		msgTableRuleConfig.setDatabaseShardingStrategyConfig(standardStrategy);
	}

}
