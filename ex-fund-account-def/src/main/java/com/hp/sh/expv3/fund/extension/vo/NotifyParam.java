package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/1/2
 */
@ApiModel("订单成功回调通知入参实体类")
public class NotifyParam {
    @ApiModelProperty("订单号")
    private String orderNo;

    @ApiModelProperty("订单金额")
    private BigDecimal orderAmount;

    @ApiModelProperty("订单币种")
    private String orderCurrency;

    @ApiModelProperty("用户支付金额")
    private BigDecimal paymentAmount;

    @ApiModelProperty("平台流水号")
    private String transactionId;

    @ApiModelProperty("状态")
    private String status;

    @ApiModelProperty("签名算法")
    private String signType;

    @ApiModelProperty("参数校验签名")
    private String sign;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(BigDecimal orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderCurrency() {
        return orderCurrency;
    }

    public void setOrderCurrency(String orderCurrency) {
        this.orderCurrency = orderCurrency;
    }

    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "NotifyParam{" +
                "orderNo='" + orderNo + '\'' +
                ", orderAmount=" + orderAmount +
                ", orderCurrency='" + orderCurrency + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", transactionId='" + transactionId + '\'' +
                ", status='" + status + '\'' +
                ", signType='" + signType + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
