package com.hp.sh.expv3.pc.grab3rdData.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/4/7
 */
public class BitfinexResponseEntity implements Serializable {
   private String symbol;
    private BigDecimal bid;
    private BigDecimal bidSize;
    private BigDecimal ask;
    private BigDecimal askSize;
    private BigDecimal dailyChange;
    private BigDecimal dailyChangeRelative;
    private BigDecimal lastPrice;
    private BigDecimal volume;
    private BigDecimal high;
    private BigDecimal low;

    public BitfinexResponseEntity() {
    }

    public BitfinexResponseEntity(String symbol, BigDecimal bid, BigDecimal bidSize, BigDecimal ask, BigDecimal askSize, BigDecimal dailyChange, BigDecimal dailyChangeRelative, BigDecimal lastPrice, BigDecimal volume, BigDecimal high, BigDecimal low) {
        this.symbol = symbol;
        this.bid = bid;
        this.bidSize = bidSize;
        this.ask = ask;
        this.askSize = askSize;
        this.dailyChange = dailyChange;
        this.dailyChangeRelative = dailyChangeRelative;
        this.lastPrice = lastPrice;
        this.volume = volume;
        this.high = high;
        this.low = low;
    }

    @Override
    public String toString() {
        return "BitfinexResponseEntity{" +
                "symbol='" + symbol + '\'' +
                ", bid=" + bid +
                ", bidSize=" + bidSize +
                ", ask=" + ask +
                ", askSize=" + askSize +
                ", dailyChange=" + dailyChange +
                ", dailyChangeRelative=" + dailyChangeRelative +
                ", lastPrice=" + lastPrice +
                ", volume=" + volume +
                ", high=" + high +
                ", low=" + low +
                '}';
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getBidSize() {
        return bidSize;
    }

    public void setBidSize(BigDecimal bidSize) {
        this.bidSize = bidSize;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getAskSize() {
        return askSize;
    }

    public void setAskSize(BigDecimal askSize) {
        this.askSize = askSize;
    }

    public BigDecimal getDailyChange() {
        return dailyChange;
    }

    public void setDailyChange(BigDecimal dailyChange) {
        this.dailyChange = dailyChange;
    }

    public BigDecimal getDailyChangeRelative() {
        return dailyChangeRelative;
    }

    public void setDailyChangeRelative(BigDecimal dailyChangeRelative) {
        this.dailyChangeRelative = dailyChangeRelative;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
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
}
