package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 提币历史Vo
 *
 * @author BaiLiJun  on 2019/12/16
 */
@ApiModel("提币历史Vo")
public class WithdrawalRecordVo {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("币种")
    private String symbol;

    @ApiModelProperty("哈希值")
    private String txHash;

    @ApiModelProperty("提币量")
    private BigDecimal amount;

    @ApiModelProperty("状态：0：已创建，1：成功，2：失败，3-同步余额,4：审核中，5-审核通过,6：审核不通过")
    private Integer status;

    @ApiModelProperty("创建时间")
    private Long ctime;

    private Date created;

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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
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

    public WithdrawalRecordVo() {
    }
}
