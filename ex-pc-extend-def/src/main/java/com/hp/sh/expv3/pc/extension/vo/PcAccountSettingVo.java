package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/24
 */
public class PcAccountSettingVo implements Serializable {

    @ApiModelProperty("资产")
    private String asset;

    @ApiModelProperty("交易对")
    private String symbol;

    @ApiModelProperty("全仓最大杠杆")
    private BigDecimal crossMaxLeverage;

    @ApiModelProperty("最大多仓杠杆")
    private BigDecimal bidMaxLeverage;

    @ApiModelProperty("最大多空杠杆")
    private BigDecimal askMaxLeverage;

    @ApiModelProperty("做多杠杆")
    private BigDecimal bidLeverage;

    @ApiModelProperty("做空杠杆")
    private BigDecimal askLeverage;

    @ApiModelProperty("全仓杠杆")
    private BigDecimal crossLeverage;

    @ApiModelProperty("1全仓 2逐仓")
    private Integer marginMode;

    public PcAccountSettingVo() {
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

    public BigDecimal getCrossMaxLeverage() {
        return crossMaxLeverage;
    }

    public void setCrossMaxLeverage(BigDecimal crossMaxLeverage) {
        this.crossMaxLeverage = crossMaxLeverage;
    }

    public BigDecimal getBidMaxLeverage() {
        return bidMaxLeverage;
    }

    public void setBidMaxLeverage(BigDecimal bidMaxLeverage) {
        this.bidMaxLeverage = bidMaxLeverage;
    }

    public BigDecimal getAskMaxLeverage() {
        return askMaxLeverage;
    }

    public void setAskMaxLeverage(BigDecimal askMaxLeverage) {
        this.askMaxLeverage = askMaxLeverage;
    }

    public BigDecimal getBidLeverage() {
        return bidLeverage;
    }

    public void setBidLeverage(BigDecimal bidLeverage) {
        this.bidLeverage = bidLeverage;
    }

    public BigDecimal getAskLeverage() {
        return askLeverage;
    }

    public void setAskLeverage(BigDecimal askLeverage) {
        this.askLeverage = askLeverage;
    }

    public BigDecimal getCrossLeverage() {
        return crossLeverage;
    }

    public void setCrossLeverage(BigDecimal crossLeverage) {
        this.crossLeverage = crossLeverage;
    }

    public Integer getMarginMode() {
        return marginMode;
    }

    public void setMarginMode(Integer marginMode) {
        this.marginMode = marginMode;
    }
}
