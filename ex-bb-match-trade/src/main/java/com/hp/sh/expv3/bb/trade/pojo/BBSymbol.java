package com.hp.sh.expv3.bb.trade.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/4
 */
public class BBSymbol implements Serializable {
    private Long id;
    /**
     * 币种
     */
    private String asset;
    /**
     * 货币对
     */
    private String symbol;
    /**
     * 步长
     */
    private BigDecimal step;
    /**
     * 是否生效
     */
    private Integer status;
    /**
     * 排序
     */
    private Integer sort;
    /**
     * 精度
     */
    private Integer precision;
    /**
     * 展示名分隔符
     */
    private String displayNameSplit;
    /**
     * 合约展示名
     */
    private String displayName;

    /**
     * 创建时间
     */
    private Long ctime;
    /**
     * 修改时间
     */
    private Long mtime;


    /**
     * 资产类型
     */
    private Integer symbolType;

    /**
     * 类型(正向、反向)
     */
    private Integer bbSymbolType;

    /**
     * 分组
     * 每组最多放2个
     */
    private Integer bbGroupId;
    /**
     * 名称
     */
    private String bbSymbolName;
    /**
     * 合约名称分隔符
     */
    private String bbSymbolNameSplit;
    /**
     * 初始化成交价
     */
    private BigDecimal defaultPrice;
    /**
     * 最新成交价
     */
    private BigDecimal lastPrice;
    /**
     * 合约面值
     */
    private BigDecimal faceValue;
    /**
     * 合约面值计价货币
     */
    private String faceCurrency;
    /**
     * 合约面值货币
     */
    private String quoteCurrency;
    /**
     * 基础货币
     */
    private String baseCurrency;
    /**
     * 结算货币
     */
    private String settleCurrency;
    /**
     * 结算金额
     */
    private BigDecimal settlePrice;
    /**
     * 权限
     */
    private Integer privilege;

    public BBSymbol() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getStep() {
        return step;
    }

    public void setStep(BigDecimal step) {
        this.step = step;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public String getDisplayNameSplit() {
        return displayNameSplit;
    }

    public void setDisplayNameSplit(String displayNameSplit) {
        this.displayNameSplit = displayNameSplit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public Integer getSymbolType() {
        return symbolType;
    }

    public void setSymbolType(Integer symbolType) {
        this.symbolType = symbolType;
    }

    public Integer getBbSymbolType() {
        return bbSymbolType;
    }

    public void setBbSymbolType(Integer bbSymbolType) {
        this.bbSymbolType = bbSymbolType;
    }

    public Integer getBbGroupId() {
        return bbGroupId;
    }

    public void setBbGroupId(Integer bbGroupId) {
        this.bbGroupId = bbGroupId;
    }

    public String getBbSymbolName() {
        return bbSymbolName;
    }

    public void setBbSymbolName(String bbSymbolName) {
        this.bbSymbolName = bbSymbolName;
    }

    public String getBbSymbolNameSplit() {
        return bbSymbolNameSplit;
    }

    public void setBbSymbolNameSplit(String bbSymbolNameSplit) {
        this.bbSymbolNameSplit = bbSymbolNameSplit;
    }

    public BigDecimal getDefaultPrice() {
        return defaultPrice;
    }

    public void setDefaultPrice(BigDecimal defaultPrice) {
        this.defaultPrice = defaultPrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public String getFaceCurrency() {
        return faceCurrency;
    }

    public void setFaceCurrency(String faceCurrency) {
        this.faceCurrency = faceCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
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

    public Integer getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Integer privilege) {
        this.privilege = privilege;
    }

    @Override
    public String toString() {
        return "BBSymbol{" +
                "id=" + id +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", step=" + step +
                ", status=" + status +
                ", sort=" + sort +
                ", precision=" + precision +
                ", displayNameSplit='" + displayNameSplit + '\'' +
                ", displayName='" + displayName + '\'' +
                ", ctime=" + ctime +
                ", mtime=" + mtime +
                ", symbolType=" + symbolType +
                ", bbSymbolType=" + bbSymbolType +
                ", bbGroupId=" + bbGroupId +
                ", bbSymbolName='" + bbSymbolName + '\'' +
                ", bbSymbolNameSplit='" + bbSymbolNameSplit + '\'' +
                ", defaultPrice=" + defaultPrice +
                ", lastPrice=" + lastPrice +
                ", faceValue=" + faceValue +
                ", faceCurrency='" + faceCurrency + '\'' +
                ", quoteCurrency='" + quoteCurrency + '\'' +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", settleCurrency='" + settleCurrency + '\'' +
                ", settlePrice=" + settlePrice +
                ", privilege=" + privilege +
                '}';
    }
}
