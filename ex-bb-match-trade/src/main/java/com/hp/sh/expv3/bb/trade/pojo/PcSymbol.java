package com.hp.sh.expv3.bb.trade.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/23
 */
public class PcSymbol implements Serializable {
    private String asset;
    private String baseCurrency;
    private String contractChineseName;
    private Integer contractGroup;
    private String contractName;
    private String contractNameSplit;
    private Integer contractType;
    private Long ctime;
    private BigDecimal defaultPrice;
    private String displayName;
    private String displayNameSplit;
    private Integer enableCancel;
    private Integer enableCreate;
    private String faceCurrency;
    private Integer faceValue;
    private Long id;
    private BigDecimal lastPrice;
    private Long mtime;
    private Integer precision;
    private Integer privilege;
    private String quoteCurrency;
    private String settleCurrency;
    private BigDecimal settlePrice;
    private BigDecimal sort;
    private Integer status;
    private Integer step;
    private String symbol;
    private Integer symbolType;

    public PcSymbol() {
    }

    @Override
    public String toString() {
        return "PcSymbol{" +
                "asset='" + asset + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", contractChineseName='" + contractChineseName + '\'' +
                ", contractGroup=" + contractGroup +
                ", contractName='" + contractName + '\'' +
                ", contractNameSplit='" + contractNameSplit + '\'' +
                ", contractType=" + contractType +
                ", ctime=" + ctime +
                ", defaultPrice=" + defaultPrice +
                ", displayName='" + displayName + '\'' +
                ", displayNameSplit='" + displayNameSplit + '\'' +
                ", enableCancel=" + enableCancel +
                ", enableCreate=" + enableCreate +
                ", faceCurrency='" + faceCurrency + '\'' +
                ", faceValue=" + faceValue +
                ", id=" + id +
                ", lastPrice=" + lastPrice +
                ", mtime=" + mtime +
                ", precision=" + precision +
                ", privilege=" + privilege +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", settleCurrency='" + settleCurrency + '\'' +
                ", settlePrice=" + settlePrice +
                ", sort=" + sort +
                ", status=" + status +
                ", step=" + step +
                ", symbol='" + symbol + '\'' +
                ", symbolType=" + symbolType +
                '}';
    }

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

    public String getContractChineseName() {
        return contractChineseName;
    }

    public void setContractChineseName(String contractChineseName) {
        this.contractChineseName = contractChineseName;
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

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
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

    public Integer getEnableCancel() {
        return enableCancel;
    }

    public void setEnableCancel(Integer enableCancel) {
        this.enableCancel = enableCancel;
    }

    public Integer getEnableCreate() {
        return enableCreate;
    }

    public void setEnableCreate(Integer enableCreate) {
        this.enableCreate = enableCreate;
    }

    public String getFaceCurrency() {
        return faceCurrency;
    }

    public void setFaceCurrency(String faceCurrency) {
        this.faceCurrency = faceCurrency;
    }

    public Integer getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Integer faceValue) {
        this.faceValue = faceValue;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
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

    public BigDecimal getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(BigDecimal settlePrice) {
        this.settlePrice = settlePrice;
    }

    public BigDecimal getSort() {
        return sort;
    }

    public void setSort(BigDecimal sort) {
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
}
