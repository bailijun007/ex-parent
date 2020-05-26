package com.hp.sh.expv3.component.dbshard;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.shardingsphere.api.sharding.complex.ComplexKeysShardingAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ExBaseShardingAlgorithm implements ComplexKeysShardingAlgorithm{
	private static final Logger logger = LoggerFactory.getLogger(ExBaseShardingAlgorithm.class);

	public abstract String getShardingColumns();
	
	protected TableInfoCache tableInfoCache;
	
	protected Collection<String> filter(Set<String> tableSet) {
		if(this.tableInfoCache==null){
			return tableSet;
		}
		Iterator<String> it = tableSet.iterator();
		while(it.hasNext()){
			String table = it.next();
			if(!this.tableInfoCache.have(table)){
				it.remove();
				logger.error("分表不存在：{}", table);
			}
		}
		return tableSet;
	}

	public void setTableInfoCache(TableInfoCache tableInfoCache) {
		this.tableInfoCache = tableInfoCache;
	}
	
}
