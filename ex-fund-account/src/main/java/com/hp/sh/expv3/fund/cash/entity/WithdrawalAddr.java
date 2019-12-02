/**
 * @author zw
 * @date 2019/8/27
 */
package com.hp.sh.expv3.fund.cash.entity;

import com.hp.sh.expv3.base.entity.UserDataEntity;

/**
 * 提现地址
 * 
 * @author lw
 *
 */
public class WithdrawalAddr extends UserDataEntity {

	private static final long serialVersionUID = 1L;

	// 资产
	private String asset;
	// 充值地址
	private String address;
	// 备注
	private String remark;
	// 启用/禁用
	private Integer enabled;

	public WithdrawalAddr() {
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

}
