package com.hp.sh.expv3.fund.cash.mq;

public class WithDrawalMsg {
	
	//用户ID
	private Long userId;
	
	//单号
	protected Long id;

	public WithDrawalMsg() {
	}

	public WithDrawalMsg(Long userId, Long id) {
		this.userId = userId;
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "NewTransfer [userId=" + userId + ", id=" + id + "]";
	}

}
