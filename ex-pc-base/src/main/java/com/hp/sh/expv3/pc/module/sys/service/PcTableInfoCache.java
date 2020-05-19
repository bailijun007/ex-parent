package com.hp.sh.expv3.pc.module.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.component.dbshard.TableInfoCache;

@Component
public class PcTableInfoCache implements TableInfoCache{

	@Lazy
	@Autowired
	private ShardTableService shardTableService;
	
	public boolean have(String table){
		return shardTableService.have(table);
	}
	
}
