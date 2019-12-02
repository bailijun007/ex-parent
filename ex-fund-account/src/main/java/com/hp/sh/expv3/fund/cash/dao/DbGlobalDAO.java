package com.hp.sh.expv3.fund.cash.dao;

import java.util.List;

/**
 * 数据库全局查询
 * @author lw
 *
 */
public interface DbGlobalDAO {
	
	public List<String> findTableName(String keyword);
	
	public Long createTable(String table);

}
