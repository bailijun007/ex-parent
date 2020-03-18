package com.hp.sh.expv3.base.entity;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseSysEntity{

	protected Long id;

	// 创建时间
	protected Long created;
	
	// 修改时间
	protected Long modified;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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
