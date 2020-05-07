package com.hp.sh.expv3.bb.module.order.entity;


import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.BaseRecordEntity;
import com.hp.sh.expv3.commons.mybatis.TxId;

/**
 * 订单日志
 */
@Deprecated
@Table(name="bb_order_log")
public class BBOrderLog extends BaseRecordEntity{
	private static final long serialVersionUID = 1L;

	private Long orderId;
	
	private Integer type;
	
	private Integer triggerType;
	
	//事务ID
	private Long txId;

    public BBOrderLog() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

	@TxId
	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}

}