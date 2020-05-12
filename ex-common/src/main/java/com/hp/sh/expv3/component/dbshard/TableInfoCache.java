package com.hp.sh.expv3.component.dbshard;

public interface TableInfoCache {

	default boolean have(String table){
		return true;
	}

}
