/**
 * 
 */
package com.hp.sh.expv3.config.db.shard;

import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;

/**
 * @author wangjg
 */
public class TableShardingByDate implements PreciseShardingAlgorithm<Comparable<?>> {
	
	private String dateFormat;
	
	public TableShardingByDate() {
	}

	@Override
	public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Comparable<?>> shardingValue) {
		String logicTableName = shardingValue.getLogicTableName();
		return logicTableName+this.getDate(shardingValue.getValue());
	}
	
	private String getDate(Object value){
		if(value instanceof String){
			return value.toString().substring(this.dateFormat.length());
		}
		
		Date date = null;
		
		if(value instanceof Long){
			date = new Date();
		}else{
			date = new Date((Long)value);
		}
		String str = DateFormatUtils.format(date, dateFormat);
		return str;
	}

}
