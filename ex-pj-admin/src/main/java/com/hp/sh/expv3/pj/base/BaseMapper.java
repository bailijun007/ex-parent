package com.hp.sh.expv3.pj.base;

/**
 * 
 * @author wangjg
 * @param <T> entity类型
 * @param <K> 主键类型
 */
public interface BaseMapper<T,K> {

	T findById(K id);

	void save(T entity);

	int update(T entity);

	int delete(K id);

}
