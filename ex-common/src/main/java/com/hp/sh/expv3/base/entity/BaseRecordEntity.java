package com.hp.sh.expv3.base.entity;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.hp.sh.expv3.component.id.utils.GeneratorName;

/**
 * 修改记录Entity
 * @author lw
 *
 */
@MappedSuperclass
public abstract class BaseRecordEntity extends BaseAccountEntity implements UserData, Serializable{

	private static final long serialVersionUID = 1L;
	
	private Long id;
	
	//用户ID
	protected Long userId;
	
	//请求ID
	protected String requestId;

	@Id
	@GeneratedValue(generator=GeneratorName.SNOWFLAKE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if(id==0 && this.id!=null){
			return;
		}
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	//	@InsertRequestId
	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
	
}
