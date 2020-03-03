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
@ApiModel("提币历史Vo")
public class WithdrawalRecordVo implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("币种")
    private String asset;

    private Long userId;


    @ApiModelProperty("哈希值")
    private String txHash;

    @ApiModelProperty("提币量")
    private BigDecimal amount;

    @ApiModelProperty("1.审核中,2.审核通过,3.失败")
    private Integer status;


    @ApiModelProperty("审批状态(4:审批中 5:审批通过:6:拒绝)")
    private Integer approvalStatus;

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

    public Integer getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(Integer approvalStatus) {
        this.approvalStatus = approvalStatus;
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

    public WithdrawalRecordVo() {
    }
}
