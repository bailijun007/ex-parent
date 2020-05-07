package com.hp.sh.expv3.bb.module.order.entity;

import javax.persistence.Table;

/**
 * 币币_用户订单成交记录
 * 
 */
@Table(name="bb_order_trade_sn")
public class BBOrderTradeSn {

	//交易序号
	private String tradeSn;
	
	private Long id;
	
	//事务ID
	private Long txId;
	
	public BBOrderTradeSn() {
	}

	public BBOrderTradeSn(String tradeSn, Long id, Long txId) {
		super();
		this.tradeSn = tradeSn;
		this.id = id;
		this.txId = txId;
	}

	public String getTradeSn() {
		return tradeSn;
	}

	public void setTradeSn(String tradeSn) {
		this.tradeSn = tradeSn;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}

}
