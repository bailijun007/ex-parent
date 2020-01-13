package com.hp.sh.expv3.pc.component.vo;

import java.math.BigDecimal;

/**
 * 仓位杠杆Vo
 *
 * @author BaiLiJun  on 2019/12/18
 */
public class PosLevelVo {

    private String asset;

    private Long ctime;

    private Integer gear;

    private Long id;
    private BigDecimal maxAmt;

    private BigDecimal maxLeverage;

    private BigDecimal minAmt;

    private BigDecimal minHoldMarginRatio;

    private Long mtime;

    private BigDecimal posHoldMarginRatio;

    private String symbol;


    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Integer getGear() {
        return gear;
    }

    public void setGear(Integer gear) {
        this.gear = gear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(BigDecimal maxAmt) {
        this.maxAmt = maxAmt;
    }

    public BigDecimal getMaxLeverage() {
        return maxLeverage;
    }

    public void setMaxLeverage(BigDecimal maxLeverage) {
        this.maxLeverage = maxLeverage;
    }

    public BigDecimal getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
    }

    public BigDecimal getMinHoldMarginRatio() {
        return minHoldMarginRatio;
    }

    public void setMinHoldMarginRatio(BigDecimal minHoldMarginRatio) {
        this.minHoldMarginRatio = minHoldMarginRatio;
    }

    public Long getMtime() {
        return mtime;
    }

    public void setMtime(Long mtime) {
        this.mtime = mtime;
    }

    public BigDecimal getPosHoldMarginRatio() {
        return posHoldMarginRatio;
    }

    public void setPosHoldMarginRatio(BigDecimal posHoldMarginRatio) {
        this.posHoldMarginRatio = posHoldMarginRatio;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public PosLevelVo() {
    }

    @Override
    public String toString() {
        return "PosLevelVo{" +
                "asset='" + asset + '\'' +
                ", ctime=" + ctime +
                ", gear=" + gear +
                ", id=" + id +
                ", maxAmt=" + maxAmt +
                ", maxLeverage=" + maxLeverage +
                ", minAmt=" + minAmt +
                ", minHoldMarginRatio=" + minHoldMarginRatio +
                ", mtime=" + mtime +
                ", posHoldMarginRatio=" + posHoldMarginRatio +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
