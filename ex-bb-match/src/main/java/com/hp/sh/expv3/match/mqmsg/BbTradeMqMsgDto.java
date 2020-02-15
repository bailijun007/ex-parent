/**
 * @author zw
 * @date 2019/8/23
 */
package com.hp.sh.expv3.match.mqmsg;

import java.math.BigDecimal;

public class BbTradeMqMsgDto {

    //id
    private Long id;
    //事务Id
    private Long matchTxId;
    //交易对
    private String symbol;
    //资产
    private String asset;

    //买卖标志
    private Integer tkBidFlag;

    //taker账户ID
    private Long tkAccountId;
    //taker订单ID
    private Long tkOrderId;

    //maker账户Id
    private Long mkAccountId;
    //maker订单ID
    private Long mkOrderId;
    //成交价格
    private BigDecimal price;
    //数量
    private BigDecimal number;
    //成交时间
    private Long tradeTime;

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
}