package com.hp.sh.expv3.pc.grab3rdData.pojo;

import io.lettuce.core.output.BooleanListOutput;

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
    // 归集成交ID
    private BigDecimal a;
    // 成交价格
    private BigDecimal p;
    // 成交数量
    private BigDecimal q;
    // 被归集的首个交易ID
    private BigDecimal f;
    // 被归集的末次交易ID
    private BigDecimal l;
    // 成交时间
    private BigDecimal T;
    // 买方是否是做市方。如true，则此次成交是一个主动卖出单，否则是一个主动买入单。
    private Boolean m;

    private Boolean M;

    public BinanceResponseData() {
    }

    @Override
    public String toString() {
        return "BinanceResponseData{" +
                "e='" + e + '\'' +
                ", E=" + E +
                ", s='" + s + '\'' +
                ", a=" + a +
                ", p=" + p +
                ", q=" + q +
                ", f=" + f +
                ", l=" + l +
                ", T=" + T +
                ", m=" + m +
                ", M=" + M +
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

    public BigDecimal getA() {
        return a;
    }

    public void setA(BigDecimal a) {
        this.a = a;
    }

    public BigDecimal getP() {
        return p;
    }

    public void setP(BigDecimal p) {
        this.p = p;
    }

    public BigDecimal getQ() {
        return q;
    }

    public void setQ(BigDecimal q) {
        this.q = q;
    }

    public BigDecimal getF() {
        return f;
    }

    public void setF(BigDecimal f) {
        this.f = f;
    }

    public BigDecimal getL() {
        return l;
    }

    public void setL(BigDecimal l) {
        this.l = l;
    }

    public BigDecimal getT() {
        return T;
    }

    public void setT(BigDecimal t) {
        T = t;
    }

    public Boolean getM() {
        return m;
    }

    public void setM(Boolean m) {
        this.m = m;
    }

    public void setE(String e) {
        this.e = e;
    }
}
