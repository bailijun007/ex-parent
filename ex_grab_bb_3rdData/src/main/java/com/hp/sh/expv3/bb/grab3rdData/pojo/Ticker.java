package com.hp.sh.expv3.bb.grab3rdData.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/4/7
 */
public class Ticker implements Serializable {
    //最高价
    private BigDecimal high;
    //成交量(最近的24小时)
    private BigDecimal vol;
    //最新成交价
    private BigDecimal last;
    //最低价
    private BigDecimal low;
    //买一价
    private BigDecimal buy;
    //卖一价
    private BigDecimal sell;

    public Ticker() {
    }

    @Override
    public String toString() {
        return "Ticker{" +
                "high=" + high +
                ", vol=" + vol +
                ", last=" + last +
                ", low=" + low +
                ", buy=" + buy +
                ", sell=" + sell +
                '}';
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getVol() {
        return vol;
    }

    public void setVol(BigDecimal vol) {
        this.vol = vol;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getBuy() {
        return buy;
    }

    public void setBuy(BigDecimal buy) {
        this.buy = buy;
    }

    public BigDecimal getSell() {
        return sell;
    }

    public void setSell(BigDecimal sell) {
        this.sell = sell;
    }
}
