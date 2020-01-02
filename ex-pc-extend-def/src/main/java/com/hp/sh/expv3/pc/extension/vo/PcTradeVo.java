package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public class PcTradeVo implements Serializable {
    @ApiModelProperty("自增主键")
    private Long id;

    @ApiModelProperty("资产")
    private String asset;

    @ApiModelProperty("交易对")
    private String symbol;

    @ApiModelProperty("事务Id")
    private Long matchTxId;

    @ApiModelProperty("taker是否买：1-是，0-否")
    private Integer tkBidFlag;

    @ApiModelProperty("taker账户ID")
    private Long tkAccountId;

    @ApiModelProperty("taker订单ID")
    private Long tkOrderId;

    @ApiModelProperty("taker是否平仓")
    private Integer tkCloseFlag;

    @ApiModelProperty("maker账户Id")
    private Long mkAccountId;

    @ApiModelProperty("maker订单ID")
    private Long mkOrderId;

    @ApiModelProperty("maker是否平仓")
    private Integer mkCloseFlag;

    @ApiModelProperty("成交价格")
    private BigDecimal price;

    @ApiModelProperty("数量")
    private BigDecimal number;

    @ApiModelProperty("成交时间")
    private Long  tradeTime;

    @ApiModelProperty("创建时间")
    private Long created;

    @ApiModelProperty("修改时间")
    private Long modified;

    public PcTradeVo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
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

    public Integer getTkCloseFlag() {
        return tkCloseFlag;
    }

    public void setTkCloseFlag(Integer tkCloseFlag) {
        this.tkCloseFlag = tkCloseFlag;
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

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
