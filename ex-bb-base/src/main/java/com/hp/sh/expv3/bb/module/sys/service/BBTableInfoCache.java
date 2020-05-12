package com.hp.sh.expv3.bb.module.sys.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.component.dbshard.TableInfoCache;

@Component
public class BBTableInfoCache implements TableInfoCache{

	@Lazy
	@Autowired
	private ShardTableService shardTableService;
	
	public boolean have(String table){
		return shardTableService.have(table);
	}
	
}
