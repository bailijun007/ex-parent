package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public class PcLiqRecordVo implements Serializable {
    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("资产")
    private String asset;

    @ApiModelProperty("交易品种")
    private String symbol;

    @ApiModelProperty("仓位ID")
    private Long posId;

    @ApiModelProperty("是否多仓(side)")
    private Integer longFlag;

    @ApiModelProperty("数量（张）")
    private BigDecimal volume;

    @ApiModelProperty("保证金")
    private BigDecimal posMargin;

    @ApiModelProperty("破产价")
    private BigDecimal bankruptPrice;

    @ApiModelProperty("创建时间")
    private Date created;

    @ApiModelProperty("修改时间")
    private Date modified;

    @ApiModelProperty("成交价格")
    private BigDecimal liqPrice;

    @ApiModelProperty("手续费")
    private BigDecimal fee;

    @ApiModelProperty("手续费率")
    private BigDecimal feeRatio;

    public PcLiqRecordVo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getPosId() {
        return posId;
    }

    public void setPosId(Long posId) {
        this.posId = posId;
    }

    public Integer getLongFlag() {
        return longFlag;
    }

    public void setLongFlag(Integer longFlag) {
        this.longFlag = longFlag;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getPosMargin() {
        return posMargin;
    }

    public void setPosMargin(BigDecimal posMargin) {
        this.posMargin = posMargin;
    }

    public BigDecimal getBankruptPrice() {
        return bankruptPrice;
    }

    public void setBankruptPrice(BigDecimal bankruptPrice) {
        this.bankruptPrice = bankruptPrice;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public BigDecimal getLiqPrice() {
        return liqPrice;
    }

    public void setLiqPrice(BigDecimal liqPrice) {
        this.liqPrice = liqPrice;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(BigDecimal feeRatio) {
        this.feeRatio = feeRatio;
    }
}
