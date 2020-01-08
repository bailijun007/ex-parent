package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@ApiModel("划转历史Vo")
public class FundTransferExtVo implements Serializable {
    @ApiModelProperty("id")
    private Long transferId;

    @ApiModelProperty("货币")
    private String asset;

    @ApiModelProperty("数据")
    private BigDecimal qty;

    @ApiModelProperty("转账时间")
    private Long ctime;

    private Long created;

    @ApiModelProperty("状态：1-成功，2-失败")
    private Integer status;

    @ApiModelProperty("转出账户")
    private String fromAccount;

    @ApiModelProperty("转入账户")
    private String toAccount;

    public Long getTransferId() {
        return transferId;
    }

    public void setTransferId(Long transferId) {
        this.transferId = transferId;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public BigDecimal getQty() {
        return qty;
    }

    public void setQty(BigDecimal qty) {
        this.qty = qty;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }

    public String getToAccount() {
        return toAccount;
    }

    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }
}
