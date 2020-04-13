package com.hp.sh.expv3.pc.grab3rdData.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/4/9
 */
public class OkResponseEntity implements Serializable {
    //卖一价
    private BigDecimal best_ask;
    //买一 价
    private BigDecimal best_bid;
    //合约名称
    private String instrument_id;
    //最新成交价
    private BigDecimal last;
    //	最新成交的数量
    private BigDecimal last_qty;
    //卖一价对应的量
    private BigDecimal best_ask_size;
    //买一价对应的数量
    private BigDecimal best_bid_size;
    //24小时最高价
    private BigDecimal high_24h;
    //	24小时最低价
    private BigDecimal low_24h;
    //系统时间戳
    private String timestamp;
    //24小时成交量，按张数统计
    private BigDecimal volume_24h;

    public OkResponseEntity() {
    }

    @Override
    public String toString() {
        return "OkResponseEntity{" +
                "best_ask=" + best_ask +
                ", best_bid=" + best_bid +
                ", instrument_id='" + instrument_id + '\'' +
                ", last=" + last +
                ", last_qty=" + last_qty +
                ", best_ask_size=" + best_ask_size +
                ", best_bid_size=" + best_bid_size +
                ", high_24h=" + high_24h +
                ", low_24h=" + low_24h +
                ", timestamp='" + timestamp + '\'' +
                ", volume_24h=" + volume_24h +
                '}';
    }

    public BigDecimal getBest_ask() {
        return best_ask;
    }

    public void setBest_ask(BigDecimal best_ask) {
        this.best_ask = best_ask;
    }

    public BigDecimal getBest_bid() {
        return best_bid;
    }

    public void setBest_bid(BigDecimal best_bid) {
        this.best_bid = best_bid;
    }

    public String getInstrument_id() {
        return instrument_id;
    }

    public void setInstrument_id(String instrument_id) {
        this.instrument_id = instrument_id;
    }

    public BigDecimal getVolume_24h() {
        return volume_24h;
    }

    public void setVolume_24h(BigDecimal volume_24h) {
        this.volume_24h = volume_24h;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getLast_qty() {
        return last_qty;
    }

    public void setLast_qty(BigDecimal last_qty) {
        this.last_qty = last_qty;
    }

    public BigDecimal getBest_ask_size() {
        return best_ask_size;
    }

    public void setBest_ask_size(BigDecimal best_ask_size) {
        this.best_ask_size = best_ask_size;
    }

    public BigDecimal getBest_bid_size() {
        return best_bid_size;
    }

    public void setBest_bid_size(BigDecimal best_bid_size) {
        this.best_bid_size = best_bid_size;
    }

    public BigDecimal getHigh_24h() {
        return high_24h;
    }

    public void setHigh_24h(BigDecimal high_24h) {
        this.high_24h = high_24h;
    }

    public BigDecimal getLow_24h() {
        return low_24h;
    }

    public void setLow_24h(BigDecimal low_24h) {
        this.low_24h = low_24h;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

}
