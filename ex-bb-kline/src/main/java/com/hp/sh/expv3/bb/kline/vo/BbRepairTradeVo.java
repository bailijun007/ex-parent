package com.hp.sh.expv3.bb.kline.vo;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/13
 */
public class BbRepairTradeVo implements Serializable {
    private long id;
    private String asset;
    private String  symbol;
    private Long  matchTxId;
    private int tkBidFlag;
    private Long tkAccountId;
    private Long tkOrderId;
    private Long mkAccountId;
    private Long mkOrderId;
    private BigDecimal price;
    private BigDecimal number;
    private Long tradeTime;
    private int makerHandleStatus;
    private int takerHandleStatus;
    private int enableFlag;
    private long created;
    private long modified;

    public Long getMatchTxId() {
        return matchTxId;
    }

    public void setMatchTxId(Long matchTxId) {
        this.matchTxId = matchTxId;
    }

    public int getTkBidFlag() {
        return tkBidFlag;
    }

    public void setTkBidFlag(int tkBidFlag) {
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

    public int getMakerHandleStatus() {
        return makerHandleStatus;
    }

    public void setMakerHandleStatus(int makerHandleStatus) {
        this.makerHandleStatus = makerHandleStatus;
    }

    public int getTakerHandleStatus() {
        return takerHandleStatus;
    }

    public void setTakerHandleStatus(int takerHandleStatus) {
        this.takerHandleStatus = takerHandleStatus;
    }

    public int getEnableFlag() {
        return enableFlag;
    }

    public void setEnableFlag(int enableFlag) {
        this.enableFlag = enableFlag;
    }

    public long getCreated() {
        return created;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public BbRepairTradeVo() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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




}
