package com.hp.sh.expv3.pc.grab3rdData.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/4/10
 */
public class BinanceResponseDataByHttps implements Serializable {

    private Long id;

    private BigDecimal price;

    private BigDecimal qty;

    private BigDecimal quoteQty;

    private Long time;

    private Boolean isBuyerMaker;

    private Boolean isBestMatch;

    public BinanceResponseDataByHttps() {
    }

    @Override
    public String toString() {
        return "BinanceResponseDataByHttps{" +
                "id=" + id +
                ", price=" + price +
                ", qty=" + qty +
                ", quoteQty=" + quoteQty +
                ", time=" + time +
                ", isBuyerMaker=" + isBuyerMaker +
                ", isBestMatch=" + isBestMatch +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
    }

    public BigDecimal getQuoteQty() {
        return quoteQty;
    }

    public void setQuoteQty(BigDecimal quoteQty) {
        this.quoteQty = quoteQty;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Boolean getBuyerMaker() {
        return isBuyerMaker;
    }

    public void setBuyerMaker(Boolean buyerMaker) {
        isBuyerMaker = buyerMaker;
    }

    public Boolean getBestMatch() {
        return isBestMatch;
    }

    public void setBestMatch(Boolean bestMatch) {
        isBestMatch = bestMatch;
    }
}
