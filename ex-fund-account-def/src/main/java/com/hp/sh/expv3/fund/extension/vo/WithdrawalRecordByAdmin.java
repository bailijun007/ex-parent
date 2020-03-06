package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 提币历史Vo
 *
 * @author BaiLiJun  on 2019/12/16
 */
public class WithdrawalRecordByAdmin implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("币种")
    private String asset;

    private Long userId;

    @ApiModelProperty("支付状态描述")
    private String payStatusDesc;

    @ApiModelProperty("哈希值")
    private String txHash;

    @ApiModelProperty("提币量")
    private BigDecimal amount;

    @ApiModelProperty("1.审核中,2.审核通过,3.失败")
    private Integer status;


    @ApiModelProperty("执行状态:0-提现中，1-提现成功，2-提现失败")
    private Integer payStatus;

    @ApiModelProperty("创建时间")
    private Long ctime;

    private Long created;

    @ApiModelProperty("提币时间")
    private Long withdrawTime;

    @ApiModelProperty("提币地址")
    private String targetAddress;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String getPayStatusDesc() {
        return payStatusDesc;
    }

    public void setPayStatusDesc(String payStatusDesc) {
        this.payStatusDesc = payStatusDesc;
    }

    public Integer getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(Integer payStatus) {
        this.payStatus = payStatus;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getWithdrawTime() {
        return withdrawTime;
    }

    public void setWithdrawTime(Long withdrawTime) {
        this.withdrawTime = withdrawTime;
    }

    public String getTargetAddress() {
        return targetAddress;
    }

    public void setTargetAddress(String targetAddress) {
        this.targetAddress = targetAddress;
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

    public WithdrawalRecordByAdmin() {
    }

}
