package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 充值记录历史
 *
 * @author BaiLiJun  on 2019/12/14
 */
@ApiModel("充值记录历史Vo")
public class DepositRecordHistoryVo implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("用户编号")
    private Long userId;

    @ApiModelProperty("币种")
    private String asset;

    @ApiModelProperty("哈希值")
    private String txHash;

    @ApiModelProperty("充币量")
    private BigDecimal volume;

    @ApiModelProperty("充币地址")
    private String address;

    @ApiModelProperty("充币地址(0:已创建，3：已到账)")
    private Integer status;

    @ApiModelProperty("更新时间")
    private Long mtime;

    @ApiModelProperty("创建时间")
    private Long ctime;

    private Long modified;

    private Long payTime;

    @ApiModelProperty("充币时间")
    private Long depositTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAsset() {
        return asset;
    }

    public void setAsset(String asset) {
        this.asset = asset;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getMtime() {
        return mtime;
    }

    public void setMtime(Long mtime) {
        this.mtime = mtime;
    }

    public Long getDepositTime() {
        return depositTime;
    }

    public void setDepositTime(Long depositTime) {
        this.depositTime = depositTime;
    }

    public DepositRecordHistoryVo() {
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getPayTime() {
        return payTime;
    }

    public void setPayTime(Long payTime) {
        this.payTime = payTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }
}
