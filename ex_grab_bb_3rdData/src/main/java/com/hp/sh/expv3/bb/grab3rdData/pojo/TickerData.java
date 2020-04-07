package com.hp.sh.expv3.bb.grab3rdData.pojo;

import java.io.Serializable;

/**
 * @author BaiLiJun  on 2020/4/7
 */
public class TickerData implements Serializable {
    private Long date;
    private String dataType;
    private String channel;
    private Ticker ticker;

    public TickerData() {
    }

    @Override
    public String toString() {
        return "TickerData{" +
                "date=" + date +
                ", dataType='" + dataType + '\'' +
                ", channel='" + channel + '\'' +
                ", ticker=" + ticker +
                '}';
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }
}
