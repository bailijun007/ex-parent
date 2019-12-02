/**
 * @author 10086
 * @date 2019/11/8
 */
package com.hp.sh.expv3.match.mqmsg;

import java.math.BigDecimal;

public class PcPosLockedMqMsgDto {

    // pos id
    private Long id;
    private Long accountId;
    private String symbol;
    private String asset;

    /**
     * 触发强平的标记价格
     */
    private BigDecimal liqMarkPrice;

    /**
     * 触发强平的标记时间
     */
    private Long liqMarkTime;

    /**
     * 等同于 longFlag，是否多仓
     */
    private Integer longFlag;
    /**
     * 强平价
     */
    private BigDecimal liqPrice;
    /**
     * 破产价
     */
    private BigDecimal bankruptPrice;
    /**
     * 需要平仓的数量
     */
    private BigDecimal amt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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

    public BigDecimal getLiqMarkPrice() {
        return liqMarkPrice;
    }

    public void setLiqMarkPrice(BigDecimal liqMarkPrice) {
        this.liqMarkPrice = liqMarkPrice;
    }

    public Long getLiqMarkTime() {
        return liqMarkTime;
    }

    public void setLiqMarkTime(Long liqMarkTime) {
        this.liqMarkTime = liqMarkTime;
    }

    public Integer getLongFlag() {
        return longFlag;
    }

    public void setLongFlag(Integer longFlag) {
        this.longFlag = longFlag;
    }

    public BigDecimal getLiqPrice() {
        return liqPrice;
    }

    public void setLiqPrice(BigDecimal liqPrice) {
        this.liqPrice = liqPrice;
    }

    public BigDecimal getBankruptPrice() {
        return bankruptPrice;
    }

    public void setBankruptPrice(BigDecimal bankruptPrice) {
        this.bankruptPrice = bankruptPrice;
    }

    public BigDecimal getAmt() {
        return amt;
    }

    public void setAmt(BigDecimal amt) {
        this.amt = amt;
    }
}
