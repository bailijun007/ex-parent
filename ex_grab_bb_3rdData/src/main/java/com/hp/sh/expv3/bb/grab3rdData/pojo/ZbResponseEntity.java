package com.hp.sh.expv3.bb.grab3rdData.pojo;

import java.io.Serializable;

/**
 * @author BaiLiJun  on 2020/4/7
 */
public class ZbResponseEntity implements Serializable {
    private Long date;
    private String dataType;
    private String channel;
    private ZbTickerData ticker;

    public ZbResponseEntity() {
    }

    @Override
    public String toString() {
        return "ZbResponseEntity{" +
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

    public ZbTickerData getTicker() {
        return ticker;
    }

    public void setTicker(ZbTickerData ticker) {
        this.ticker = ticker;
    }
}
