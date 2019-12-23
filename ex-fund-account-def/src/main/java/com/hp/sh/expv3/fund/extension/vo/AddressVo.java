package com.hp.sh.expv3.fund.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public class AddressVo implements Serializable {
    @ApiModelProperty("充币地址")
    private String address;

    @ApiModelProperty("用户id")
    private Long userId;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public AddressVo() {
    }
}
