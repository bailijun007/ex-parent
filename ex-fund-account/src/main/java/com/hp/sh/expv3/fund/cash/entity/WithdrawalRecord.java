package com.hp.sh.expv3.fund.cash.entity;

import java.math.BigDecimal;

/**
 * 提现记录
 * @author wangjg
 *
 */
public class WithdrawalRecord extends PaymentRecord {
	
	private static final long serialVersionUID = 1L;
	
	//应收提币手续费
	private BigDecimal fee;
	
	//已押手续费
	private BigDecimal feeMargin;

	/*
	 * @see ApprovalStatus#*
	 * 审批状态 
	 */
	private Integer approvalStatus;
	
	public WithdrawalRecord() {
		
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getFeeMargin() {
		return feeMargin;
	}

	public void setFeeMargin(BigDecimal feeMargin) {
		this.feeMargin = feeMargin;
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

}
