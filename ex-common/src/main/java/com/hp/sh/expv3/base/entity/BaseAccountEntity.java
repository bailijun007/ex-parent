package com.hp.sh.expv3.base.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * 账户信息Entity 
 * @author lw
 *
 */
@MappedSuperclass
public abstract class BaseAccountEntity implements UserData, Serializable{

	private static final long serialVersionUID = 1L;

	public static final String USERID_PROPERTY = "userId";
	
	// 创建时间
	private Long created;
	
	// 修改时间
	private Long modified;

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public Long getModified() {
		return modified;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}
	
}
