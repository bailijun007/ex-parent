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

import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingValue;

import com.google.common.collect.Range;
import com.hp.sh.expv3.component.dbshard.impl.SnowId2DateShard;

/**
 * @author wangjg
 */
public class TableShardingByDate extends ExBaseShardingAlgorithm {

	protected final String idColumnName;
	
	protected final String dateColumnName;
	
	protected final IdDateShard idDateShard;
	
	public TableShardingByDate() {
		this("id", "created", new SnowId2DateShard());
	}

	public TableShardingByDate(String idColumnName, String dateColumnName) {
		this(idColumnName, dateColumnName, new SnowId2DateShard());
	}

	public TableShardingByDate(String idColumnName, String dateColumnName, IdDateShard idDateShard) {
		this.idColumnName = idColumnName;
		this.dateColumnName = dateColumnName;
		this.idDateShard = idDateShard;
	}

	@Override
	@SuppressWarnings("all")
	public Collection doSharding(Collection availableTargetNames, ComplexKeysShardingValue shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		
		Map cvMap = shardingValue.getColumnNameAndShardingValuesMap();
		Map crMap = shardingValue.getColumnNameAndRangeValuesMap();
		Set<String> tableSet = new HashSet<String>();
		
		List<String> dateShards = this.getDateShards(shardingValue);
		
		if(dateShards!=null){
			for(String dateShard : dateShards){
				String tableName = getTableName(logicTableName, dateShard);
				tableSet.add(tableName);
			}
		}
		
		return this.filter(tableSet);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected List<String> getDateShards(ComplexKeysShardingValue shardingValue){
		Map cvMap = shardingValue.getColumnNameAndShardingValuesMap();
		Map crMap = shardingValue.getColumnNameAndRangeValuesMap();
		
		Collection<Long> createds = (Collection<Long>) cvMap.get(dateColumnName);
		Collection ids = (Collection) cvMap.get(idColumnName);

		List<String> dateShards = new ArrayList<String>();
		
		if(ids!=null){
			for(Object id: ids){
				dateShards.add(idDateShard.getDateShardById(id));
			}
		}
		
		if(createds!=null){
			for(Long created : createds){
				dateShards.add(DateShardUtils.getDateShard(created));
			}
		}
		
		Range<Long> createdRange = (Range<Long>) crMap.get(dateColumnName);
		Range<Long> tradeTimeRange = (Range<Long>) crMap.get("trade_time");
		dateShards.addAll(getRangeDates(createdRange));
		dateShards.addAll(getRangeDates(tradeTimeRange));
		
		return dateShards;
	}
	
	public static String getTableName(String logicTableName, String date) {
		String suffix = "_" + date;
		String tableName = logicTableName + suffix;
		tableName = tableName.toLowerCase();
		return tableName;
	}

	private static List<String> getRangeDates(Range<Long> dateRange) {
		if(dateRange==null){
			return Collections.emptyList();
		}
		if(!dateRange.hasLowerBound()){
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

	public String getShardingColumns(){
		String colums = StringUtils.joinWith(",", this.idColumnName, this.dateColumnName);
		return colums;
	}
	
}
