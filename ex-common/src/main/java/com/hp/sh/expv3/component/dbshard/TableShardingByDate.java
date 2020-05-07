/**
 * 
 */
package com.hp.sh.expv3.component.dbshard;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

/**
 * @author wangjg
 */
public class TableShardingByDate implements PreciseShardingAlgorithm<Comparable<?>> {
	
	private final String dateFormat = "yyyyMM";
	
	public TableShardingByDate() {
	}

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Comparable<?>> shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		return getTableName(logicTableName, this.getDate(shardingValue.getValue()));
	}
	
	public static String getTableName(String logicTableName, String date) {
		String tableName = logicTableName +"_"+ date;
		tableName = tableName.toLowerCase();
		return tableName;
	}
	
	private String getDate(Object value){
		if(value instanceof String){
			return value.toString().substring(this.dateFormat.length());
		}
		
		Date date = null;
		
		if(value instanceof Long){
			date = new Date((Long)value);
		}else{
			date = (Date)value;
		}
		String str = DateFormatUtils.format(date, dateFormat);
		return str;
	}

}
