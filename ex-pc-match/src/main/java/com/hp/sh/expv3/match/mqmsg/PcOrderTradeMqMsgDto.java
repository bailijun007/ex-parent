/**
 * @author zw
 * @date 2019/8/23
 */
package com.hp.sh.expv3.match.mqmsg;

import java.math.BigDecimal;

public class PcOrderTradeMqMsgDto {

    private Integer makerFlag;
    private Long accountId;
    private String symbol;
    private String asset;
    private BigDecimal price;
    private BigDecimal number;
    private Long matchTxId;
    private Long orderId;
    private Long tradeTime;
    private Long tradeId;
    private Long opponentOrderId;

    public Integer getMakerFlag() {
        return makerFlag;
    }

    public void setMakerFlag(Integer makerFlag) {
        this.makerFlag = makerFlag;
    }

    public Long getTradeId() {
        return tradeId;
    }

    public void setTradeId(Long tradeId) {
        this.tradeId = tradeId;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
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

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    public Long getOpponentOrderId() {
        return opponentOrderId;
    }

    public void setOpponentOrderId(Long opponentOrderId) {
        this.opponentOrderId = opponentOrderId;
    }
}