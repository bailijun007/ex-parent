package com.hp.sh.expv3.bb.component.vo;

import java.math.BigDecimal;

public class AssetVO {

	private Long id;
	
	private Integer chainAppointId;

	private String realName;

	private String displayName;

	private String chainName;
	
	private BigDecimal precision;
	
	private Integer privilege;
	
	private Integer status;

	private String icon;

	private String iconImg;

	private Integer sort;

	private BigDecimal minDepositVolume;

	private BigDecimal minWithdrawVolume;

	private BigDecimal withdrawFee;

	private BigDecimal c2cFee;

	private String chainTransactionUrl;

	private Integer dwType;

	private Integer enableFlagPcAccount;

	private Integer enableFlagBbAccount;

	private Integer enableFlagPcMarket;

	private Integer enableFlagBbMarket;

	private Long ctime;

	private Long mtime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getChainAppointId() {
		return chainAppointId;
	}

	public void setChainAppointId(Integer chainAppointId) {
		this.chainAppointId = chainAppointId;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getChainName() {
		return chainName;
	}

	public void setChainName(String chainName) {
		this.chainName = chainName;
	}

	public BigDecimal getPrecision() {
		return precision;
	}

	public void setPrecision(BigDecimal precision) {
		this.precision = precision;
	}

	public Integer getPrivilege() {
		return privilege;
	}

	public void setPrivilege(Integer privilege) {
		this.privilege = privilege;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getIconImg() {
		return iconImg;
	}

	public void setIconImg(String iconImg) {
		this.iconImg = iconImg;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public BigDecimal getMinDepositVolume() {
		return minDepositVolume;
	}

	public void setMinDepositVolume(BigDecimal minDepositVolume) {
		this.minDepositVolume = minDepositVolume;
	}

	public BigDecimal getMinWithdrawVolume() {
		return minWithdrawVolume;
	}

	public void setMinWithdrawVolume(BigDecimal minWithdrawVolume) {
		this.minWithdrawVolume = minWithdrawVolume;
	}

	public BigDecimal getWithdrawFee() {
		return withdrawFee;
	}

	public void setWithdrawFee(BigDecimal withdrawFee) {
		this.withdrawFee = withdrawFee;
	}

	public BigDecimal getC2cFee() {
		return c2cFee;
	}

	public void setC2cFee(BigDecimal c2cFee) {
		this.c2cFee = c2cFee;
	}

	public String getChainTransactionUrl() {
		return chainTransactionUrl;
	}

	public void setChainTransactionUrl(String chainTransactionUrl) {
		this.chainTransactionUrl = chainTransactionUrl;
	}

	public Integer getDwType() {
		return dwType;
	}

	public void setDwType(Integer dwType) {
		this.dwType = dwType;
	}

	public Integer getEnableFlagPcAccount() {
		return enableFlagPcAccount;
	}

	public void setEnableFlagPcAccount(Integer enableFlagPcAccount) {
		this.enableFlagPcAccount = enableFlagPcAccount;
	}

	public Integer getEnableFlagBbAccount() {
		return enableFlagBbAccount;
	}

	public void setEnableFlagBbAccount(Integer enableFlagBbAccount) {
		this.enableFlagBbAccount = enableFlagBbAccount;
	}

	public Integer getEnableFlagPcMarket() {
		return enableFlagPcMarket;
	}

	public void setEnableFlagPcMarket(Integer enableFlagPcMarket) {
		this.enableFlagPcMarket = enableFlagPcMarket;
	}

	public Integer getEnableFlagBbMarket() {
		return enableFlagBbMarket;
	}

	public void setEnableFlagBbMarket(Integer enableFlagBbMarket) {
		this.enableFlagBbMarket = enableFlagBbMarket;
	}

	public Long getCtime() {
		return ctime;
	}

	public void setCtime(Long ctime) {
		this.ctime = ctime;
	}

	public Long getMtime() {
		return mtime;
	}

	public void setMtime(Long mtime) {
		this.mtime = mtime;
	}

	@Override
	public String toString() {
		return "BBAssetVO [id=" + id + ", chainAppointId=" + chainAppointId + ", realName=" + realName
				+ ", displayName=" + displayName + ", chainName=" + chainName + ", precision=" + precision
				+ ", privilege=" + privilege + ", status=" + status + ", icon=" + icon + ", iconImg=" + iconImg
				+ ", sort=" + sort + ", minDepositVolume=" + minDepositVolume + ", minWithdrawVolume="
				+ minWithdrawVolume + ", withdrawFee=" + withdrawFee + ", c2cFee=" + c2cFee + ", chainTransactionUrl="
				+ chainTransactionUrl + ", dwType=" + dwType + ", enableFlagPcAccount=" + enableFlagPcAccount
				+ ", enableFlagBbAccount=" + enableFlagBbAccount + ", enableFlagPcMarket=" + enableFlagPcMarket
				+ ", enableFlagBbMarket=" + enableFlagBbMarket + ", ctime=" + ctime + ", mtime=" + mtime + "]";
	}

}
