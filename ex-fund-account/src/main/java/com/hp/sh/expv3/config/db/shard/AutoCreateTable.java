package com.hp.sh.expv3.config.db.shard;

import java.util.Set;

public class AutoCreateTable {
	
	private Set<String> tableCache;
	
	public void checkAndCreate(String table){
		if(!tableCache.contains(table)){
			this.create(table);
			this.tableCache.add(table);
		}
	}

	private void create(String table) {
		
	}

}
