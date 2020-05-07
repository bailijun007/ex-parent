package com.hp.sh.expv3.bb.module.sys.dao;

import org.apache.ibatis.annotations.Param;

/**
 * 数据库全局查询
 * @author lw
 *
 */
public interface DbGlobalDAO {
	
	public String findTableName(@Param("dbName") String dbName, @Param("tableName") String tableName);

	public Long createAccountRecordTable(@Param("tableName") String tableName);
	
	public Long createOrderTable(@Param("tableName") String tableName);
	
	public Long createOrderTradeTable(@Param("tableName") String tableName);

}
