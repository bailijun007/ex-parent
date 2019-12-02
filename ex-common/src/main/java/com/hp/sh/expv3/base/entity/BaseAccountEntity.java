package com.hp.sh.expv3.base.entity;

import java.io.Serializable;
import java.util.Date;

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
	private Date created;
	
	// 修改时间
	private Date modified;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}
	
}
