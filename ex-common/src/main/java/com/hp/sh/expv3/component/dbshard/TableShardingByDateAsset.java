/**
 * 
 */
package com.hp.sh.expv3.component.dbshard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import com.google.common.collect.Range;
import com.hp.sh.expv3.component.context.IdGeneratorContext;

/**
 * @author wangjg
 */
public class TableShardingByDateAsset implements ComplexKeysShardingAlgorithm {
	
	public TableShardingByDateAsset() {
	}

	@Override
	@SuppressWarnings("all")
	public Collection doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		
		Map cvMap = shardingValue.getColumnNameAndShardingValuesMap();
		Map crMap = shardingValue.getColumnNameAndRangeValuesMap();
		Collection<String> assets = (Collection<String>) cvMap.get("asset");
		Collection<Long> createds = (Collection<Long>) cvMap.get("created");
		Collection<Long> ids = (Collection<Long>) cvMap.get("id");
		Range<Long> createdRange = (Range<Long>) crMap.get("created");
		Set<String> tableSet = new HashSet<String>();
		
		List<Long> dateMills = new ArrayList<Long>();
		
		if(ids!=null){
			for(Long id: ids){
				Long time = IdGeneratorContext.getSnowIdTime(id);
				dateMills.add(time);
			}
		}
		
		if(createds!=null){
			for(Long created : createds){
				dateMills.add(created);
			}
		}
		
		Collection<String> rangeDateStr = getRangeDates(createdRange);
		
		if(rangeDateStr!=null){
			for(String asset : assets){
				for(String date : rangeDateStr){
					String tableName = getTableName(logicTableName, asset, date);
					tableSet.add(tableName);
				}
			}
		}
		if(dateMills!=null){
			for(String asset : assets){
				for(Long created : dateMills){
					String tableName = getTableName(logicTableName, asset, DateShardUtils.getDate(created));
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

	private static List<String> getRangeDates(Range<Long> dateRange) {
		if(dateRange==null){
			return Collections.emptyList();
		}
		Long start = dateRange.lowerEndpoint();
		Long end = dateRange.hasUpperBound()?dateRange.upperEndpoint():System.currentTimeMillis();
		long now = System.currentTimeMillis();
		if(end > now){
			end = now;
		}
		
		return DateShardUtils.getRangeDates(start, end);

	}

}
