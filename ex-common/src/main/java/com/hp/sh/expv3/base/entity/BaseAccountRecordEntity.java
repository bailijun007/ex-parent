package com.hp.sh.expv3.base.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * 账户信息Entity 
 * @author lw
 *
 */
@MappedSuperclass
public abstract class BaseAccountRecordEntity extends BaseAccountEntity implements UserData, Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	//请求ID
	protected long requestId;

	@Id
	@GeneratedValue(generator="snowflake")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}
	
}
