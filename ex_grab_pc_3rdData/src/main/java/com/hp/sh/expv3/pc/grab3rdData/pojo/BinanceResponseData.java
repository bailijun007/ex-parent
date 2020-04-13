package com.hp.sh.expv3.pc.grab3rdData.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/4/10
 */
public class BinanceResponseData implements Serializable {

    // 事件类型
    private String e;
    // 事件时间（毫秒）
    private Long E;
    // 交易对
    private String s;
    // 最新成交价格
    private BigDecimal c;
    // 24小时前开始第一笔成交价格
    private BigDecimal o;
    // 24小时内最高成交价
    private BigDecimal h;
    // 24小时内最低成交加
    private BigDecimal l;
    //成交量
    private BigDecimal v;
    // 成交额
    private BigDecimal q;

    public BinanceResponseData() {
    }

    @Override
    public String toString() {
        return "BinanceResponseData{" +
                "e='" + e + '\'' +
                ", E=" + E +
                ", s='" + s + '\'' +
                ", c=" + c +
                ", o=" + o +
                ", h=" + h +
                ", l=" + l +
                ", v=" + v +
                ", q=" + q +
                '}';
    }

    public String getE() {
        return e;
    }

    public void setE(Long e) {
        E = e;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public BigDecimal getC() {
        return c;
    }

    public void setC(BigDecimal c) {
        this.c = c;
    }

    public BigDecimal getO() {
        return o;
    }

    public void setO(BigDecimal o) {
        this.o = o;
    }

    public BigDecimal getH() {
        return h;
    }

    public void setH(BigDecimal h) {
        this.h = h;
    }

    public BigDecimal getL() {
        return l;
    }

    public void setL(BigDecimal l) {
        this.l = l;
    }

    public BigDecimal getV() {
        return v;
    }

    public void setV(BigDecimal v) {
        this.v = v;
    }

    public BigDecimal getQ() {
        return q;
    }

    public void setQ(BigDecimal q) {
        this.q = q;
    }

    public void setE(String e) {
        this.e = e;
    }
}
