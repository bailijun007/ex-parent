package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public class WithdrawalAddrParam implements Serializable {
    @ApiModelProperty(value = "用户id",required = true,example = "2")
    private Long userId;

    @ApiModelProperty(value = "币种",required = true,example = "ETH")
    private String asset;

    @ApiModelProperty(value = "当前页",required = true,example = "1")
    private Integer pageNo;

    @ApiModelProperty(value = "页行数",required = true,example = "10")
    private Integer pageSize;

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

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
