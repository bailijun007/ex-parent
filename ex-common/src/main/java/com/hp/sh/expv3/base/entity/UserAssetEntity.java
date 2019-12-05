package com.hp.sh.expv3.base.entity;

/**
 * 用户资产Entity
 * @author wangjg
 *
 */
public class UserAssetEntity {
	//用户ID
	private Long userId;
	
	//资产类型
	private String asset;

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAsset() {
		return asset;
	}

	public void setAsset(String asset) {
		this.asset = asset;
	}
	
}
