package com.hp.sh.expv3.bb.trade.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/4
 */
public class BBKLine implements Serializable {
    private Long id;
    private String asset;
    private String symbol;
    private int frequence;// 频率，固定一分钟
    private long timestamp; // 分钟时间戳
    private BigDecimal volume; //  number 累加
    private BigDecimal highPrice; // max(price)
    private BigDecimal lowPrice;// min(price)
    private BigDecimal openPrice;// 按成交时间第一个 first(price)
    private BigDecimal closePrice;// 按成交时间最后一个 last(price)
    private Long created;
    private Long modified;
    public BBKLine() {

    }

    public Long getCreated() {
        return created;
    }

    @Override
    public String toString() {
        return "BBKLine{" +
                "id=" + id +
                ", asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", frequence=" + frequence +
                ", timestamp=" + timestamp +
                ", volume=" + volume +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", openPrice=" + openPrice +
                ", closePrice=" + closePrice +
                ", created=" + created +
                ", modified=" + modified +
                '}';
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getFrequence() {
        return frequence;
    }

    public void setFrequence(int frequence) {
        this.frequence = frequence;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }
}
