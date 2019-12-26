/**
 * @author corleone
 * @date 2018/7/9 0009
 */
package com.hp.sh.expv3.match.msg;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class BookMsgDto extends BaseMessageDto {
    public BookMsgDto() {
    }

    public static class BookEntry implements Serializable {
        /**
         * 订单Id
         */
        Long orderId;
        /**
         * bid: buy ; ask : sell
         */
        Integer bidFlag;
        /**
         * 挂单的价格
         */
        BigDecimal price;
        /**
         * 挂单的数量
         */
        BigDecimal number;

        public BookEntry() {
        }

        public BookEntry(Long orderId, BigDecimal price, BigDecimal amt, Integer bidFlag) {
            this.orderId = orderId;
            this.bidFlag = bidFlag;
            this.price = price;
            this.number = amt;
        }

        public BookEntry(Long orderId, BigDecimal amt) {
            this.orderId = orderId;
            this.number = amt;
        }

        public Long getOrderId() {
            return orderId;
        }

        public BookEntry setOrderId(Long orderId) {
            this.orderId = orderId;
            return this;
        }

        public Integer getBidFlag() {
            return bidFlag;
        }

        public BookEntry setBidFlag(Integer bidFlag) {
            this.bidFlag = bidFlag;
            return this;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public BookEntry setPrice(BigDecimal price) {
            this.price = price;
            return this;
        }

        public BigDecimal getNumber() {
            return number;
        }

        public void setNumber(BigDecimal number) {
            this.number = number;
        }
    }

    /**
     * @see {@linkplain 0|1}
     */
    private Integer resetFlag;

    /**
     * 交易标的
     */
    private String symbol;
    private String asset;
    private BigDecimal lastPrice;
    private List<BookEntry> orders;

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }


    public Integer getResetFlag() {
        return resetFlag;
    }

    public void setResetFlag(Integer resetFlag) {
        this.resetFlag = resetFlag;
    }

    public List<BookEntry> getOrders() {
        return orders;
    }

    public BookMsgDto setOrders(List<BookEntry> orders) {
        this.orders = orders;
        return this;
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
}
