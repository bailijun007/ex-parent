package com.hp.sh.expv3.fund.cash.entity;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/1/19
 */
public class AssetVo {
    private int chainAppointId;

    private String chainName;

    private int chainSymbolId;

    private String chainTransactionUrl;

    private BigDecimal ctcFee;

    private Long ctime;

    private String displayName;

    private int dwType;

    private String icon;

    private String iconImg;

    private Long id;

    private BigDecimal minDepositVolume;

    private Long mtime;

    private int precision;

    private int privilege;

    private String realName;

    private int sort;
    private int status;
    private String symbol;
    private BigDecimal withdrawFee;

    public int getChainSymbolId() {
        return chainSymbolId;
    }

    public void setChainSymbolId(int chainSymbolId) {
        this.chainSymbolId = chainSymbolId;
    }

    public int getChainAppointId() {
        return chainAppointId;
    }

    public void setChainAppointId(int chainAppointId) {
        this.chainAppointId = chainAppointId;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public String getChainTransactionUrl() {
        return chainTransactionUrl;
    }

    public void setChainTransactionUrl(String chainTransactionUrl) {
        this.chainTransactionUrl = chainTransactionUrl;
    }

    public BigDecimal getCtcFee() {
        return ctcFee;
    }

    public void setCtcFee(BigDecimal ctcFee) {
        this.ctcFee = ctcFee;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getDwType() {
        return dwType;
    }

    public void setDwType(int dwType) {
        this.dwType = dwType;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMinDepositVolume() {
        return minDepositVolume;
    }

    public void setMinDepositVolume(BigDecimal minDepositVolume) {
        this.minDepositVolume = minDepositVolume;
    }

    public Long getMtime() {
        return mtime;
    }

    public void setMtime(Long mtime) {
        this.mtime = mtime;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getWithdrawFee() {
        return withdrawFee;
    }

    public void setWithdrawFee(BigDecimal withdrawFee) {
        this.withdrawFee = withdrawFee;
    }
}
