package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/24
 */
public class PcAccountRecordLogVo implements Serializable {
    @ApiModelProperty("金额")
    private BigDecimal volume;

    @ApiModelProperty("已付费用")
    private BigDecimal fee;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("交易类型")
    private String tradeType;

    @ApiModelProperty("成交数量")
    private BigDecimal tradeAmt;

    @ApiModelProperty("委托数量")
    private BigDecimal orderAmt;

    @ApiModelProperty("未成交数量")
    private BigDecimal noTradeAmt;

    @ApiModelProperty("成交价格")
    private BigDecimal tradePrice;

    @ApiModelProperty("佣金费率")
    private BigDecimal feeRatio;

    @ApiModelProperty("委托类型")
    private Integer orderType;

    @ApiModelProperty("委托价格")
    private BigDecimal orderPrice;

    @ApiModelProperty("委托id")
    private Long orderId;

    @ApiModelProperty("时间")
    private Long time;

    @ApiModelProperty("引用对象Id")
    private Long refId;

    public PcAccountRecordLogVo() {
    }

    public Long getTime() {
        return time;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getOrderAmt() {
        return orderAmt;
    }

    public void setOrderAmt(BigDecimal orderAmt) {
        this.orderAmt = orderAmt;
    }

    public BigDecimal getNoTradeAmt() {
        return noTradeAmt;
    }

    public void setNoTradeAmt(BigDecimal noTradeAmt) {
        this.noTradeAmt = noTradeAmt;
    }

    public BigDecimal getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(BigDecimal tradePrice) {
        this.tradePrice = tradePrice;
    }

    public BigDecimal getFeeRatio() {
        return feeRatio;
    }

    public void setFeeRatio(BigDecimal feeRatio) {
        this.feeRatio = feeRatio;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
