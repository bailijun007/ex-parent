package com.hp.sh.expv3.base.entity;

import static javax.persistence.GenerationType.IDENTITY;

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
	@Id
	@GeneratedValue(strategy = IDENTITY)
	private Long id;
	
	//请求ID
	protected long requestId;

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
