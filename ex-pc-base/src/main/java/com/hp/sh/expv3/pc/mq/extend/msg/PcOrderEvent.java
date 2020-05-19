package com.hp.sh.expv3.pc.mq.extend.msg;

import com.hp.sh.expv3.pc.module.order.entity.PcOrder;

public class PcOrderEvent {

	private PcOrder pcOrder;

	public PcOrderEvent(PcOrder pcOrder, Object pcOrderLog) {
		super();
		this.pcOrder = pcOrder;
	}

	public PcOrder getPcOrder() {
		return pcOrder;
	}

	public void setPcOrder(PcOrder pcOrder) {
		this.pcOrder = pcOrder;
	}
}
