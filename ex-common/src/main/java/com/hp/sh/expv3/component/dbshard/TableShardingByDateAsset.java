/**
 * 
 */
package com.hp.sh.expv3.component.dbshard;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

/**
 * @author wangjg
 */
public class TableShardingByDateAsset extends TableShardingByDate implements ComplexKeysShardingAlgorithm {

	public TableShardingByDateAsset() {
		super("id", "created");
	}

	@Override
	@SuppressWarnings("all")
	public Collection doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		
		Map cvMap = shardingValue.getColumnNameAndShardingValuesMap();
		Map crMap = shardingValue.getColumnNameAndRangeValuesMap();
		Collection<String> assets = (Collection<String>) cvMap.get("asset");
		Set<String> tableSet = new HashSet<String>();
		
		List<String> dateShards = this.getDateShards(shardingValue);
		
		if(dateShards!=null){
			for(String asset : assets){
				for(String dateShard : dateShards){
					String tableName = getTableName(logicTableName, asset, dateShard);
					tableSet.add(tableName);
				}
			}
		}
		
		return tableSet;
	}
	
	public static String getTableName(String logicTableName, String asset, String date) {
		String suffix = "_" + asset + "_" + date;
		String tableName = logicTableName + suffix;
		tableName = tableName.toLowerCase();
		return tableName;
	}


}
