package com.hp.sh.expv3.pc.extension.vo;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public class PcAccountLogVo implements Serializable {
    @ApiModelProperty("事件类型")
    private Integer type;

    @ApiModelProperty("用户Id")
    private Long userId;

    @ApiModelProperty("资产")
    private String asset;

    @ApiModelProperty("交易品种")
    private String symbol;

    @ApiModelProperty("引用对象Id")
    private Long refId;

    @ApiModelProperty("时间")
    private Long time;

    public PcAccountLogVo() {
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
