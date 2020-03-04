package com.hp.sh.expv3.bb.mq.msg.vo;

import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderLog;

public class BBOrderEvent {

	private BBOrder bBOrder;

	private BBOrderLog bBOrderLog;

	public BBOrderEvent(BBOrder bBOrder, BBOrderLog bBOrderLog) {
		super();
		this.bBOrder = bBOrder;
		this.bBOrderLog = bBOrderLog;
	}

	public BBOrder getPcOrder() {
		return bBOrder;
	}

	public void setPcOrder(BBOrder bBOrder) {
		this.bBOrder = bBOrder;
	}

	public BBOrderLog getPcOrderLog() {
		return bBOrderLog;
	}

	public void setPcOrderLog(BBOrderLog bBOrderLog) {
		this.bBOrderLog = bBOrderLog;
	}

}
