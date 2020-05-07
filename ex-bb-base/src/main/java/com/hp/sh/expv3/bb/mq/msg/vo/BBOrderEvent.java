package com.hp.sh.expv3.bb.mq.msg.vo;

import com.hp.sh.expv3.bb.module.order.entity.BBOrder;

public class BBOrderEvent {

	private BBOrder order;

	private Object orderLog;

	public BBOrderEvent(BBOrder bBOrder, Object bBOrderLog) {
		super();
		this.order = bBOrder;
		this.orderLog = bBOrderLog;
	}

	public BBOrder getOrder() {
		return order;
	}

	public void setOrder(BBOrder bBOrder) {
		this.order = bBOrder;
	}

	public Object getOrderLog() {
		return orderLog;
	}

	public void setOrderLog(Object bBOrderLog) {
		this.orderLog = bBOrderLog;
	}

}
