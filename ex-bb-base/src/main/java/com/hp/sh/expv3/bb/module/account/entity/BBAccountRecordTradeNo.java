package com.hp.sh.expv3.bb.module.account.entity;

import javax.persistence.Table;

/**
 * 币币_账户明细
 * 
 * @author lw
 *
 */
@Table(name="bb_account_record_trade_no")
public class BBAccountRecordTradeNo{

	//调用方支付单号
	private String tradeNo;

	//记录Id
	private Long recordId;

	//事务ID
	private Long txId;

	public BBAccountRecordTradeNo() {
	}

	public BBAccountRecordTradeNo(String tradeNo, Long recordId, Long txId) {
		this.tradeNo = tradeNo;
		this.recordId = recordId;
		this.txId = txId;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Long getRecordId() {
		return recordId;
	}

	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}

	public Long getTxId() {
		return txId;
	}

	public void setTxId(Long txId) {
		this.txId = txId;
	}
	
}
