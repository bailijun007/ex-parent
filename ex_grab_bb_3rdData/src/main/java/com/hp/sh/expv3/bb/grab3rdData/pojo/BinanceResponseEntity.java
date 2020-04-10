package com.hp.sh.expv3.bb.grab3rdData.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/4/10
 */
public class BinanceResponseEntity implements Serializable {

    private String stream;

   private List<BinanceResponseData> data;

    public String getStream() {
        return stream;
    }

    public BinanceResponseEntity() {
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public List<BinanceResponseData> getData() {
        return data;
    }

    public void setData(List<BinanceResponseData> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BinanceResponseEntity{" +
                "stream='" + stream + '\'' +
                ", data=" + data +
                '}';
    }
}
