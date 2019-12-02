package com.hp.sh.expv3.fund.cash.entity;

/**
 * 提现记录
 * @author wangjg
 *
 */
public class WithdrawalRecord extends PaymentRecord {
	
	private static final long serialVersionUID = 1L;

	//审批状态
	private Integer approvalStatus;
	
	public WithdrawalRecord() {
		
	}

	public Integer getApprovalStatus() {
		return approvalStatus;
	}

	public void setApprovalStatus(Integer approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

}
