package com.hp.sh.expv3.bb.mq.msg.vo;

import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderLog;

public class BBOrderEvent {

	private BBOrder order;

	private BBOrderLog orderLog;

	public BBOrderEvent(BBOrder bBOrder, BBOrderLog bBOrderLog) {
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

	public BBOrderLog getOrderLog() {
		return orderLog;
	}

	public void setOrderLog(BBOrderLog bBOrderLog) {
		this.orderLog = bBOrderLog;
	}

}
