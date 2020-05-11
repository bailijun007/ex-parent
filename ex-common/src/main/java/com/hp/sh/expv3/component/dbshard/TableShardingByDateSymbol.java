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
public class TableShardingByDateSymbol extends TableShardingByDate implements ComplexKeysShardingAlgorithm {
	
	public TableShardingByDateSymbol() {
		super("id", "created");
	}

	@Override
	@SuppressWarnings("all")
	public Collection doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		
		Map cvMap = shardingValue.getColumnNameAndShardingValuesMap();
		Map crMap = shardingValue.getColumnNameAndRangeValuesMap();
		Collection<String> assets = (Collection<String>) cvMap.get("asset");
		Collection<String> symbols = (Collection<String>) cvMap.get("symbol");
		Set<String> tableSet = new HashSet<String>();
		
		List<String> dateShards = this.getDateShards(shardingValue);
		
		if(dateShards!=null){
			for(String asset : assets){
				for(String symbol : symbols){
					for(String dateShard : dateShards){
						String tableName = getTableName(logicTableName, asset, symbol, dateShard);
						tableSet.add(tableName);
					}
				}
			}
		}
		
		return tableSet;
	}
	
	public static String getTableName(String logicTableName, String asset, String symbol, String date) {
		String suffix = "_" + asset + "__" + symbol + "_" + date;
		String tableName = logicTableName + suffix;
		tableName = tableName.toLowerCase();
		return tableName;
	}

}
