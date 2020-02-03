package com.hp.sh.expv3.bb.component.vo;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/19
 */
public class PcContractVO {

    private String asset;
    private String baseCurrency;
    private Integer contractGroup;
    private String contractName;
    private String contractNameSplit;
    private Integer contractType;
    private Long ctime;
    private Integer defaultPrice;
    private String displayName;
    private String displayNameSplit;
    private String faceCurrency;
    private BigDecimal faceValue;
    private Long id;
    private Integer lastPrice;
    private Long mtime;
    private Integer precision;
    private Integer privilege;
    private String quoteCurrency;
    private String settleCurrency;
    private Integer settlePrice;
    private Integer sort;
    private Integer status;
    private Integer step;
    private String symbol;
    private Integer symbolType;

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Integer getContractGroup() {
        return contractGroup;
    }

    public void setContractGroup(Integer contractGroup) {
        this.contractGroup = contractGroup;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractNameSplit() {
        return contractNameSplit;
    }

    public void setContractNameSplit(String contractNameSplit) {
        this.contractNameSplit = contractNameSplit;
    }

    public Integer getContractType() {
        return contractType;
    }

    public void setContractType(Integer contractType) {
        this.contractType = contractType;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Integer getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(Integer defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayNameSplit() {
        return displayNameSplit;
    }

    public void setDisplayNameSplit(String displayNameSplit) {
        this.displayNameSplit = displayNameSplit;
    }

    public String getFaceCurrency() {
        return faceCurrency;
    }

    public void setFaceCurrency(String faceCurrency) {
        this.faceCurrency = faceCurrency;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(Integer lastPrice) {
        this.lastPrice = lastPrice;
    }

    public Long getMtime() {
        return mtime;
    }

    public void setMtime(Long mtime) {
        this.mtime = mtime;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public String getSettleCurrency() {
        return settleCurrency;
    }

    public void setSettleCurrency(String settleCurrency) {
        this.settleCurrency = settleCurrency;
    }

    public Integer getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(Integer settlePrice) {
        this.settlePrice = settlePrice;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStep() {
        return step;
    }

    public void setStep(Integer step) {
        this.step = step;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Integer getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(Integer symbolType) {
        this.symbolType = symbolType;
    }

    public PcContractVO() {
    }

	@Override
	public String toString() {
		return "PcContractVO [asset=" + asset + ", baseCurrency=" + baseCurrency + ", contractGroup=" + contractGroup
				+ ", contractName=" + contractName + ", contractNameSplit=" + contractNameSplit + ", contractType="
				+ contractType + ", ctime=" + ctime + ", defaultPrice=" + defaultPrice + ", displayName=" + displayName
				+ ", displayNameSplit=" + displayNameSplit + ", faceCurrency=" + faceCurrency + ", faceValue="
				+ faceValue + ", id=" + id + ", lastPrice=" + lastPrice + ", mtime=" + mtime + ", precision="
				+ precision + ", privilege=" + privilege + ", quoteCurrency=" + quoteCurrency + ", settleCurrency="
				+ settleCurrency + ", settlePrice=" + settlePrice + ", sort=" + sort + ", status=" + status + ", step="
				+ step + ", symbol=" + symbol + ", symbolType=" + symbolType + "]";
	}
}
