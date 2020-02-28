package com.hp.sh.expv3.bb.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public class BbAccountRecordVo implements Serializable {
    @ApiModelProperty("自增主键")
    private Long id;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("资产类型")
    private String asset;


    @ApiModelProperty("流水号")
    private String sn;

    @ApiModelProperty("类型：1 收入,-1 支出")
    private Integer type;


    @ApiModelProperty("本笔金额")
    private BigDecimal amount;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("本笔余额")
    private BigDecimal balance;

    @ApiModelProperty("调用方支付单号")
    private String tradeNo;

    @ApiModelProperty("交易类型：1-资金转入，2-资金转出，3-下订单，4-撤单，4-追加保证金，5-平仓收益")
    private Integer tradeType;

    @ApiModelProperty("序号")
    private Long serialNo;

    @ApiModelProperty("关联对象的ID")
    private Long associatedId;

    @ApiModelProperty("事务ID")
    private Long txId;

    @ApiModelProperty("请求ID")
    private String requestId;


    @ApiModelProperty("修改时间")
    private Long modified;

    @ApiModelProperty("创建时间")
    private Long created;

    public BbAccountRecordVo() {
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
    }

    public Integer getTradeType() {
        return tradeType;
    }

    public void setTradeType(Integer tradeType) {
        this.tradeType = tradeType;
    }

    public Long getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Long serialNo) {
        this.serialNo = serialNo;
    }

    public Long getAssociatedId() {
        return associatedId;
    }

    public void setAssociatedId(Long associatedId) {
        this.associatedId = associatedId;
    }

    public Long getTxId() {
        return txId;
    }

    public void setTxId(Long txId) {
        this.txId = txId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "BbAccountRecordVo{" +
                "id=" + id +
                ", userId=" + userId +
                ", asset='" + asset + '\'' +
                ", sn='" + sn + '\'' +
                ", type=" + type +
                ", amount=" + amount +
                ", remark='" + remark + '\'' +
                ", balance=" + balance +
                ", tradeNo='" + tradeNo + '\'' +
                ", tradeType=" + tradeType +
                ", serialNo=" + serialNo +
                ", associatedId=" + associatedId +
                ", txId=" + txId +
                ", requestId='" + requestId + '\'' +
                ", modified=" + modified +
                ", created=" + created +
                '}';
    }
}
