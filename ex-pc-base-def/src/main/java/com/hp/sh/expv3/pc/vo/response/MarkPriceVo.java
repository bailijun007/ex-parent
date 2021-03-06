package com.hp.sh.expv3.pc.vo.response;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 标记价格
 *
 * @author BaiLiJun  on 2019/12/23
 */
public class MarkPriceVo {
    @ApiModelProperty("标记价格")
    private BigDecimal markPrice;
    @ApiModelProperty("时间戳")
    private Long time;

    public MarkPriceVo() {
	}

	public MarkPriceVo(BigDecimal markPrice, Long time) {
		this.markPrice = markPrice;
		this.time = time;
	}

	public BigDecimal getMarkPrice() {
        return markPrice;
    }

    public void setMarkPrice(BigDecimal markPrice) {
        this.markPrice = markPrice;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
