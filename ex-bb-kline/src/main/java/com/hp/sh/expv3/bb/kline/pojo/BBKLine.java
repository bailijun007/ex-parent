package com.hp.sh.expv3.bb.kline.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/3/4
 */
public class BBKLine implements Serializable {
    private String asset, symbol;
    private int frequence;// 频率，固定一分钟
    private long minute; // 毫秒时间戳
    private BigDecimal volume; //  number 累加
    //        BigDecimal amt;//
    private BigDecimal high; // max(price)
    private BigDecimal low;// min(price)
    private BigDecimal open;// 按成交时间第一个 first(price)
    private BigDecimal close;// 按成交时间最后一个 last(price)

    public BBKLine() {

    }

    @Override
    public String toString() {
        return "BBKLine{" +
                "asset='" + asset + '\'' +
                ", symbol='" + symbol + '\'' +
                ", frequence=" + frequence +
                ", volume=" + volume +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", close=" + close +
                '}';
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

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getOpen() {
        return open;
    }

    public void setOpen(BigDecimal open) {
        this.open = open;
    }

    public BigDecimal getClose() {
        return close;
    }

    public void setClose(BigDecimal close) {
        this.close = close;
    }

    public long getMinute() {
        return minute;
    }

    public void setMinute(long minute) {
        this.minute = minute;
    }
}
