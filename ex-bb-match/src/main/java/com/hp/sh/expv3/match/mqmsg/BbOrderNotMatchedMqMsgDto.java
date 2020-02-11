/**
 * @author 10086
 * @date 2019/10/22
 */
package com.hp.sh.expv3.match.mqmsg;

public class BbOrderNotMatchedMqMsgDto {

    private Long accountId;
    private String asset;
    private String symbol;

    private Long orderId;

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

}