package com.hp.sh.expv3.base.mapper;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.entity.BaseBizEntity;
import com.hp.sh.expv3.base.entity.UserData;
import com.hp.sh.expv3.base.entity.UserDataEntity;

/**
 * 
 * @param <T> entity类型
 * @param <K> 主键类型
 */
public interface BaseUserDataMapper<T extends UserData,K>{
	
	void save(T entity);

	int update(T entity);

	public T findById(@Param(UserDataEntity.USERID_PROPERTY) Long userId, @Param(BaseBizEntity.ID_PROPERTY) K id);

}
