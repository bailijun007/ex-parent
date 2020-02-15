package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public class BbOrderTradeVo implements Serializable {
    @ApiModelProperty("自增主键")
    private Long id;

    //用户ID
    @ApiModelProperty("用户ID")
    protected Long userId;
    //资产
    @ApiModelProperty("资产")
    private String asset;

    //合约交易品种
    @ApiModelProperty("合约交易品种")
    private String symbol;

    //成交价
    @ApiModelProperty("成交价")
    private BigDecimal price;

    //成交量（个数）
    @ApiModelProperty("成交量（个数）")
    private BigDecimal volume;

    private Integer bidFlag;

    //交易序号
    @ApiModelProperty("交易序号")
    private String tradeSn;

    //交易ID
    @ApiModelProperty("交易ID")
    private Long tradeId;

    //订单ID
    @ApiModelProperty("订单ID")
    private Long orderId;

    // 1-marker， 0-taker
    @ApiModelProperty("1-marker， 0-taker")
    private Integer makerFlag;

    //成交时间
    @ApiModelProperty("成交时间")
    private Long tradeTime;


    //手续费收取人
    @ApiModelProperty("手续费收取人")
    private Long feeCollectorId;

    //手续费率
    @ApiModelProperty("手续费率")
    private BigDecimal feeRatio;

    //手续费
    @ApiModelProperty("手续费")
    private BigDecimal fee;

    //未成交（张数）
    @ApiModelProperty("未成交（张数）")
    private BigDecimal remainVolume;

    //撮合事务Id
    @ApiModelProperty("撮合事务Id")
    private Long matchTxId;

    //事务ID
    @ApiModelProperty("事务ID")
    private Long txId;

    @Transient
    private Integer logType;

    @ApiModelProperty("修改时间")
    private Long modified;

    @ApiModelProperty("创建时间")
    private Long created;

    public BbOrderTradeVo() {
    }

    @Override
    public String toString() {
        return "BbOrderTradeVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +
                ", volume=" + volume +
                ", bidFlag=" + bidFlag +
                ", tradeSn='" + tradeSn + '\'' +
                ", tradeId=" + tradeId +
                ", orderId=" + orderId +
                ", makerFlag=" + makerFlag +
                ", tradeTime=" + tradeTime +
                ", feeCollectorId=" + feeCollectorId +
                ", feeRatio=" + feeRatio +
                ", fee=" + fee +
                ", remainVolume=" + remainVolume +
                ", matchTxId=" + matchTxId +
                ", txId=" + txId +
                ", logType=" + logType +
                ", modified=" + modified +
                ", created=" + created +
                '}';
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public Integer getBidFlag() {
        return bidFlag;
    }

    public void setBidFlag(Integer bidFlag) {
        this.bidFlag = bidFlag;
    }

    public String getTradeSn() {
        return tradeSn;
    }

    public void setTradeSn(String tradeSn) {
        this.tradeSn = tradeSn;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getMakerFlag() {
        return makerFlag;
    }

    public void setMakerFlag(Integer makerFlag) {
        this.makerFlag = makerFlag;
    }

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Long getFeeCollectorId() {
        return feeCollectorId;
    }

    public void setFeeCollectorId(Long feeCollectorId) {
        this.feeCollectorId = feeCollectorId;
    }

    public BigDecimal getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(BigDecimal feeRatio) {
        this.feeRatio = feeRatio;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public BigDecimal getRemainVolume() {
        return remainVolume;
    }

    public void setRemainVolume(BigDecimal remainVolume) {
        this.remainVolume = remainVolume;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public Long getTxId() {
        return txId;
    }

    public void setTxId(Long txId) {
        this.txId = txId;
    }

    public Integer getLogType() {
        return logType;
    }

    public void setLogType(Integer logType) {
        this.logType = logType;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }
}
