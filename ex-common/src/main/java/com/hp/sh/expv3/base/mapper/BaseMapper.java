package com.hp.sh.expv3.base.mapper;

/**
 * 
 * @param <T> entity类型
 * @param <K> 主键类型
 */
public interface BaseMapper<T,K> {

	void save(T entity);

	int update(T entity);

}
