package com.hp.sh.expv3.bb.extension.sys.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 数据库全局查询
 * @author lw
 *
 */
public interface DbGlobalDAO {
	
	public String findTableName(@Param("dbName") String dbName, @Param("tableName") String tableName);

	public List<String> findTableByKeyword(@Param("dbName") String dbName, @Param("keyword") String keyword);

}
