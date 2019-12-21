package com.hp.sh.expv3.pc.strategy.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/21
 */
public class LastFaceValue implements Serializable {

    //最新一期面值
    private BigDecimal faceValue;
    //时间戳
    private  Long time;

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
