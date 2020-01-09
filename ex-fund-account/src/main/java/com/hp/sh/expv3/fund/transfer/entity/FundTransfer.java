package com.hp.sh.expv3.fund.transfer.entity;

import java.math.BigDecimal;

import com.hp.sh.expv3.base.entity.BaseAccountRecordEntity;

/**
 * 资金划转
 * @author wangjg
 *
 */
public class FundTransfer extends BaseAccountRecordEntity {
	
	private static final long serialVersionUID = 1L;
	
	public static final int STATUS_NEW = 1; // 创建
	
	public static final int STATUS_SRC_COMPLETE = 3; // 源账号完成
	
	public static final int STATUS_TARGET_COMPLETE = 7; // 目标账号完成
	
	public static final int STATUS_SUCCESS = 15; // 成功
	
	public static final int STATUS_FAIL = 16; // 失败
	
	//用户ID
	private Long userId;
	
	//单号
	protected String sn;
	
	//资产
	protected String asset;

	//支付/收款金额
	protected BigDecimal amount;
	
	//源账户类型
    private long srcAccountType;
    
    //目标账户类型
    private long targetAccountType;
	
	//源账户ID
    private long srcAccountId;
    
    //目标账户ID
    private long targetAccountId;

    //备注
    private String remark;
    
    //错误信息
    private String errorInfo;

    //状态 @see #STATUS_*
    private Integer status;

	public FundTransfer() {
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public long getSrcAccountType() {
		return srcAccountType;
	}

	public void setSrcAccountType(long srcAccountType) {
		this.srcAccountType = srcAccountType;
	}

	public long getTargetAccountType() {
		return targetAccountType;
	}

	public void setTargetAccountType(long targetAccountType) {
		this.targetAccountType = targetAccountType;
	}

	public long getSrcAccountId() {
		return srcAccountId;
	}

	public void setSrcAccountId(long srcAccountId) {
		this.srcAccountId = srcAccountId;
	}

	public long getTargetAccountId() {
		return targetAccountId;
	}

	public void setTargetAccountId(long targetAccountId) {
		this.targetAccountId = targetAccountId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	@Override
	public String toString() {
		return "FundTransfer [userId=" + userId + ", sn=" + sn + ", asset=" + asset + ", amount=" + amount
				+ ", srcAccountType=" + srcAccountType + ", targetAccountType=" + targetAccountType + ", srcAccountId="
				+ srcAccountId + ", targetAccountId=" + targetAccountId + ", remark=" + remark + ", errorInfo="
				+ errorInfo + ", status=" + status + "]";
	}

}
