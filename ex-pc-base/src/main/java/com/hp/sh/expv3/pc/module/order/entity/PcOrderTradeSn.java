package com.hp.sh.expv3.pc.module.order.entity;

import javax.persistence.Table;

/**
 * 永续合约_用户订单成交记录
 * 
 */
@Table(name="pc_order_trade_sn")
public class PcOrderTradeSn {

	//交易序号
	private String tradeSn;
	
	private Long id;
	
	//事务ID
	private Long txId;
	
	public PcOrderTradeSn() {
	}

	public PcOrderTradeSn(String tradeSn, Long id, Long txId) {
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
