package com.hp.sh.expv3.pj.module.sys.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

/**
 * 数据库全局查询
 * @author lw
 *
 */
public interface DbGlobalDAO {
	
	public String findTableName(@Param("dbName") String dbName, @Param("tableName") String tableName);
	
	public String findFirstTableByKeyword(@Param("dbName") String dbName, @Param("keyword") String keyword);
	
	public List<String> findTableByKeyword(@Param("dbName") String dbName, @Param("keyword") String keyword);

}
