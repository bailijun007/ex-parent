/**
 * @author zw
 * @date 2019/8/20
 */
package com.hp.sh.expv3.match.bo;

import java.math.BigDecimal;

public class PcOrder2CancelBo {

    private Long accountId;
    private Long orderId;
    private String symbol;
    private String asset;
    private BigDecimal cancelNumber;

    public BigDecimal getCancelNumber() {
        return cancelNumber;
    }

    public void setCancelNumber(BigDecimal cancelNumber) {
        this.cancelNumber = cancelNumber;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

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
}
