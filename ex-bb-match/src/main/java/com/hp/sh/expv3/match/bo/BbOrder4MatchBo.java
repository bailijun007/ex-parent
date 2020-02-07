/**
 * @author zw
 * @date 2019/8/22
 */
package com.hp.sh.expv3.match.bo;

import java.math.BigDecimal;

public class BbOrder4MatchBo {

    private Long orderId;
    private String asset;
    private String symbol;
    private Long accountId;
    // todo bb
    private Integer closeFlag;
    private Integer bidFlag;
    private Integer timeInForce;
    private BigDecimal number;
    private BigDecimal filledNumber;
    private BigDecimal displayNumber;
    private BigDecimal price;
    private Integer orderType;
    private Long orderTime;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public Integer getBidFlag() {
        return bidFlag;
    }

    public void setBidFlag(Integer bidFlag) {
        this.bidFlag = bidFlag;
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

    public Integer getCloseFlag() {
        return closeFlag;
    }

    public void setCloseFlag(Integer closeFlag) {
        this.closeFlag = closeFlag;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public BigDecimal getFilledNumber() {
        return filledNumber;
    }

    public void setFilledNumber(BigDecimal filledNumber) {
        this.filledNumber = filledNumber;
    }

    public BigDecimal getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(BigDecimal displayNumber) {
        this.displayNumber = displayNumber;
    }

    public Integer getTimeInForce() {
        return timeInForce;
    }

    public void setTimeInForce(Integer timeInForce) {
        this.timeInForce = timeInForce;
    }
}
