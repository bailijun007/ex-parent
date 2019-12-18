/**
 * @author 10086
 * @date 2019/10/22
 */
package com.hp.sh.expv3.match.mqmsg;

import java.math.BigDecimal;

public class PcOrderMqMsgDto {

    private String asset;
    private String symbol;

    private Long orderId;
    private Long accountId;
    private Integer closeFlag;
    private Integer bidFlag;
    private Integer timeInForce;

    private BigDecimal number;
    private BigDecimal displayNumber;
    private BigDecimal filledNumber;
    private BigDecimal price;
    private Integer orderType;
    private Long orderTime;

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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(Integer closeFlag) {
        this.closeFlag = closeFlag;
    }

    public Integer getBidFlag() {
        return bidFlag;
    }

    public void setBidFlag(Integer bidFlag) {
        this.bidFlag = bidFlag;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public BigDecimal getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(BigDecimal displayNumber) {
        this.displayNumber = displayNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getFilledNumber() {
        return filledNumber;
    }

    public void setFilledNumber(BigDecimal filledNumber) {
        this.filledNumber = filledNumber;
    }

    public Integer getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(Integer timeInForce) {
        this.timeInForce = timeInForce;
    }
}