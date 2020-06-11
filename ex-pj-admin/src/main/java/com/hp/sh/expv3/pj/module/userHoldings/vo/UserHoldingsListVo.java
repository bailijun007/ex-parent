package com.hp.sh.expv3.pj.module.userHoldings.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/6/11
 */
public class UserHoldingsListVo implements Serializable {
    private String symbol;

    private Long id;

    private String username;

    private Long orderTime;

    private BigDecimal holdingNumber;

    private Integer orderType;

    private BigDecimal orderTotal;

    private BigDecimal avgTradePrice;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Long orderTime) {
        this.orderTime = orderTime;
    }

    public BigDecimal getHoldingNumber() {
        return holdingNumber;
    }

    public void setHoldingNumber(BigDecimal holdingNumber) {
        this.holdingNumber = holdingNumber;
    }

    public Integer getOrderType() {
        return orderType;
    }

    public void setOrderType(Integer orderType) {
        this.orderType = orderType;
    }

    public BigDecimal getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(BigDecimal orderTotal) {
        this.orderTotal = orderTotal;
    }

    public BigDecimal getAvgTradePrice() {
        return avgTradePrice;
    }

    public void setAvgTradePrice(BigDecimal avgTradePrice) {
        this.avgTradePrice = avgTradePrice;
    }
}
