/**
 * @author zw
 * @date 2019/8/23
 */
package com.hp.sh.expv3.match.bo;

import java.math.BigDecimal;

/**
 * 永续合约_成交(撮合结果)
 */
public class PcTradeBo {

    private Long id;
    private Long matchTxId;
    private String symbol;
    private String asset;

    private Integer tkBidFlag;

    private Long tkAccountId;
    private Long tkOrderId;
    private Integer tkCloseFlag;

    private Long mkAccountId;
    private Long mkOrderId;
    private Integer mkCloseFlag;

    private BigDecimal price;
    private BigDecimal number;
    private Long tradeTime;

    public BigDecimal getNumber() {
        return number;
    }

    public void setNumber(BigDecimal number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getTkBidFlag() {
        return tkBidFlag;
    }

    public void setTkBidFlag(Integer tkBidFlag) {
        this.tkBidFlag = tkBidFlag;
    }

    public Integer getTkCloseFlag() {
        return tkCloseFlag;
    }

    public void setTkCloseFlag(Integer tkCloseFlag) {
        this.tkCloseFlag = tkCloseFlag;
    }

    public Long getTkAccountId() {
        return tkAccountId;
    }

    public void setTkAccountId(Long tkAccountId) {
        this.tkAccountId = tkAccountId;
    }

    public Long getTkOrderId() {
        return tkOrderId;
    }

    public void setTkOrderId(Long tkOrderId) {
        this.tkOrderId = tkOrderId;
    }


    public Long getMkAccountId() {
        return mkAccountId;
    }

    public void setMkAccountId(Long mkAccountId) {
        this.mkAccountId = mkAccountId;
    }

    public Long getMkOrderId() {
        return mkOrderId;
    }

    public void setMkOrderId(Long mkOrderId) {
        this.mkOrderId = mkOrderId;
    }

    public Integer getMkCloseFlag() {
        return mkCloseFlag;
    }

    public void setMkCloseFlag(Integer mkCloseFlag) {
        this.mkCloseFlag = mkCloseFlag;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Long getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(Long tradeTime) {
        this.tradeTime = tradeTime;
    }

    @Override
    public String toString() {
        return "PcTradeBo{" +
                "id=" + id +
                ", matchTxId=" + matchTxId +
                ", symbol='" + symbol + '\'' +
                ", asset='" + asset + '\'' +
                ", tkBidFlag=" + tkBidFlag +
                ", tkCloseFlag=" + tkCloseFlag +
                ", tkAccountId=" + tkAccountId +
                ", tkOrderId=" + tkOrderId +
                ", mkAccountId=" + mkAccountId +
                ", mkOrderId=" + mkOrderId +
                ", mkCloseFlag=" + mkCloseFlag +
                ", price=" + price +
                ", number=" + number +
                ", tradeTime=" + tradeTime +
                '}';
    }
}
