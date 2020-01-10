/**
 * @author zw
 * @date 2019/8/20
 */
package com.hp.sh.expv3.match.bo;

public class BbOrderNotMatchedBo {

    private Long accountId;
    private Long orderId;
    private String symbol;
    private String asset;

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
