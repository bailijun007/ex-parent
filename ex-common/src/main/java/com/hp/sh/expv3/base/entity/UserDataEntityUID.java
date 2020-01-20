package com.hp.sh.expv3.base.entity;

/**
 * 用户数据唯一标识
 * @author wangjg
 *
 */
public class UserDataEntityUID {
	
	protected Long id;
	//用户ID
	protected Long userId;

	public UserDataEntityUID() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "UserDataEntityUID [id=" + id + ", userId=" + userId + "]";
	}
	
}
