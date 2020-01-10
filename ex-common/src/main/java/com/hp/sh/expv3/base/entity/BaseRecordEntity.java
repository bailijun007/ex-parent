package com.hp.sh.expv3.base.entity;

import java.io.Serializable;

import javax.persistence.MappedSuperclass;

/**
 * 修改记录Entity
 * @author lw
 *
 */
@MappedSuperclass
public abstract class BaseRecordEntity extends UserDataEntity implements UserData, Serializable{

	private static final long serialVersionUID = 1L;
	
	//请求ID
	protected String requestId;
	
	//	@InsertRequestId
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
}
