package com.hp.sh.expv3.pc.mq.extend.msg;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;

public class PcOrderMsg {
	
	private PcOrder order;

	public PcOrderMsg() {
	}

	public PcOrderMsg(PcOrder order) {
		this.order = order;
	}

	public PcOrder getOrder() {
		return order;
	}

	public void setOrder(PcOrder order) {
		this.order = order;
	}
	
}
