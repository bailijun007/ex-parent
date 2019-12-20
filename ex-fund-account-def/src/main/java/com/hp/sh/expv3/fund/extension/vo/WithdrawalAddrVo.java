package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@ApiModel("查询提币地址Vo")
public class WithdrawalAddrVo implements Serializable {
    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("货币")
    private String asset;

    @ApiModelProperty("地址")
    private String address;

    @ApiModelProperty("备注")
    private String remark;


    private Integer enabled;


    private Long userId;


    private Date created;


    private Date modified;


    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
