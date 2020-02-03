package com.hp.sh.expv3.bb.mq.extend.msg;

import com.hp.sh.expv3.bb.module.order.entity.PcOrder;
import com.hp.sh.expv3.bb.module.order.entity.PcOrderLog;

public class PcOrderEvent {

	private PcOrder pcOrder;

	private PcOrderLog pcOrderLog;

	public PcOrderEvent(PcOrder pcOrder, PcOrderLog pcOrderLog) {
		super();
		this.pcOrder = pcOrder;
		this.pcOrderLog = pcOrderLog;
	}

	public PcOrder getPcOrder() {
		return pcOrder;
	}

	public void setPcOrder(PcOrder pcOrder) {
		this.pcOrder = pcOrder;
	}

	public PcOrderLog getPcOrderLog() {
		return pcOrderLog;
	}

	public void setPcOrderLog(PcOrderLog pcOrderLog) {
		this.pcOrderLog = pcOrderLog;
	}

}
