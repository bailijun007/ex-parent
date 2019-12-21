package com.hp.sh.expv3.base.entity;

import javax.persistence.MappedSuperclass;
import java.math.BigDecimal;

/**
 * 典型的用户数据Entity 
 * @author lw
 *
 */
@MappedSuperclass
public abstract class UserDataEntity extends BaseBizEntity implements UserData{

	private static final long serialVersionUID = 1L;
	
	public static final String USERID_PROPERTY = "userId";
	
	//用户ID
	protected Long userId;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}


}
