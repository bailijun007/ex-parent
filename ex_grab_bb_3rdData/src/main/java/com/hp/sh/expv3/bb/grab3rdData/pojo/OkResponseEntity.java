package com.hp.sh.expv3.bb.grab3rdData.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/4/9
 */
public class OkResponseEntity implements Serializable {

    private BigDecimal best_ask;
    private BigDecimal best_bid;
    private String instrument_id;
    private String product_id;
    private BigDecimal last;
    private BigDecimal last_qty;
    private BigDecimal ask;
    private BigDecimal best_ask_size;
    private BigDecimal bid;
    private BigDecimal best_bid_size;
    private BigDecimal open_24h;
    private BigDecimal high_24h;
    private BigDecimal low_24h;
    private BigDecimal base_volume_24h;
    private String timestamp;
    private BigDecimal quote_volume_24h;

    public OkResponseEntity() {
    }

    @Override
    public String toString() {
        return "OkResponseEntity{" +
                "best_ask=" + best_ask +
                ", best_bid=" + best_bid +
                ", instrument_id='" + instrument_id + '\'' +
                ", product_id='" + product_id + '\'' +
                ", last=" + last +
                ", last_qty=" + last_qty +
                ", ask=" + ask +
                ", best_ask_size=" + best_ask_size +
                ", bid=" + bid +
                ", best_bid_size=" + best_bid_size +
                ", open_24h=" + open_24h +
                ", high_24h=" + high_24h +
                ", low_24h=" + low_24h +
                ", base_volume_24h=" + base_volume_24h +
                ", timestamp='" + timestamp + '\'' +
                ", quote_volume_24h=" + quote_volume_24h +
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

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
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

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getBest_ask_size() {
        return best_ask_size;
    }

    public void setBest_ask_size(BigDecimal best_ask_size) {
        this.best_ask_size = best_ask_size;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getBest_bid_size() {
        return best_bid_size;
    }

    public void setBest_bid_size(BigDecimal best_bid_size) {
        this.best_bid_size = best_bid_size;
    }

    public BigDecimal getOpen_24h() {
        return open_24h;
    }

    public void setOpen_24h(BigDecimal open_24h) {
        this.open_24h = open_24h;
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

    public BigDecimal getBase_volume_24h() {
        return base_volume_24h;
    }

    public void setBase_volume_24h(BigDecimal base_volume_24h) {
        this.base_volume_24h = base_volume_24h;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getQuote_volume_24h() {
        return quote_volume_24h;
    }

    public void setQuote_volume_24h(BigDecimal quote_volume_24h) {
        this.quote_volume_24h = quote_volume_24h;
    }
}
