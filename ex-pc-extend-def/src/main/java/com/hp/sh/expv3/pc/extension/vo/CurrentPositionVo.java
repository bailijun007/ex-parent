package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public class CurrentPositionVo implements Serializable {
    @ApiModelProperty("仓位id")
    private Long id;
    @ApiModelProperty("用户id")
    private Long userId;
    @ApiModelProperty("资产")
    private String asset;
    @ApiModelProperty("交易对")
    private String symbol;
    @ApiModelProperty("1:全仓,2:逐仓")
    private Integer marginMode;

    @ApiModelProperty("可平数量")
    private BigDecimal availQty;

    @ApiModelProperty("平均开仓价")
    private BigDecimal entryPrice;

    @ApiModelProperty("杠杆")
    private BigDecimal leverage;

    @ApiModelProperty("预估强平价")
    private BigDecimal liquidationPrice;

    @ApiModelProperty("仓位保证金")
    private BigDecimal posMargin;

    @ApiModelProperty("保证金率")
    private BigDecimal posMarginRatio;

    @ApiModelProperty("维持保证金率")
    private BigDecimal maintMarginRatio;

    @ApiModelProperty("持仓量")
    private BigDecimal qty;

    @ApiModelProperty("收益率")
    private BigDecimal posPnlRatio;

    @ApiModelProperty("已实现盈亏")
    private BigDecimal realisedPnl;

    @ApiModelProperty("未实现盈亏")
    private BigDecimal pnl;

    @ApiModelProperty("1多,0空")
    private Integer bidFlag;

    @ApiModelProperty("打开自动追加保证金,0.关闭 ")
    private Integer autoIncreaseFlag;

    @ApiModelProperty("开仓时间")
    private Long ctime;

    @ApiModelProperty("累计成交量")
    private  BigDecimal accuVolume;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Integer getMarginMode() {
        return marginMode;
    }

    public void setMarginMode(Integer marginMode) {
        this.marginMode = marginMode;
    }

    public BigDecimal getAvailQty() {
        return availQty;
    }

    public void setAvailQty(BigDecimal availQty) {
        this.availQty = availQty;
    }

    public BigDecimal getEntryPrice() {
        return entryPrice;
    }

    public void setEntryPrice(BigDecimal entryPrice) {
        this.entryPrice = entryPrice;
    }

    public BigDecimal getLeverage() {
        return leverage;
    }

    public void setLeverage(BigDecimal leverage) {
        this.leverage = leverage;
    }

    public BigDecimal getLiquidationPrice() {
        return liquidationPrice;
    }

    public void setLiquidationPrice(BigDecimal liquidationPrice) {
        this.liquidationPrice = liquidationPrice;
    }

    public BigDecimal getPosMargin() {
        return posMargin;
    }

    public void setPosMargin(BigDecimal posMargin) {
        this.posMargin = posMargin;
    }

    public BigDecimal getPosMarginRatio() {
        return posMarginRatio;
    }

    public void setPosMarginRatio(BigDecimal posMarginRatio) {
        this.posMarginRatio = posMarginRatio;
    }

    public BigDecimal getMaintMarginRatio() {
        return maintMarginRatio;
    }

    public void setMaintMarginRatio(BigDecimal maintMarginRatio) {
        this.maintMarginRatio = maintMarginRatio;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getPosPnlRatio() {
        return posPnlRatio;
    }

    public void setPosPnlRatio(BigDecimal posPnlRatio) {
        this.posPnlRatio = posPnlRatio;
    }

    public BigDecimal getRealisedPnl() {
        return realisedPnl;
    }

    public void setRealisedPnl(BigDecimal realisedPnl) {
        this.realisedPnl = realisedPnl;
    }

    public BigDecimal getPnl() {
        return pnl;
    }

    public void setPnl(BigDecimal pnl) {
        this.pnl = pnl;
    }

    public Integer getBidFlag() {
        return bidFlag;
    }

    public void setBidFlag(Integer bidFlag) {
        this.bidFlag = bidFlag;
    }

    public Integer getAutoIncreaseFlag() {
        return autoIncreaseFlag;
    }

    public void setAutoIncreaseFlag(Integer autoIncreaseFlag) {
        this.autoIncreaseFlag = autoIncreaseFlag;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public BigDecimal getAccuVolume() {
        return accuVolume;
    }

    public void setAccuVolume(BigDecimal accuVolume) {
        this.accuVolume = accuVolume;
    }
}
