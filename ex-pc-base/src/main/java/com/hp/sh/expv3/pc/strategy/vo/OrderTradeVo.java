package com.hp.sh.expv3.pc.strategy.vo;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.strategy.data.OrderTrade;

public class OrderTradeVo implements OrderTrade{

	private BigDecimal volume;
	
	private BigDecimal price;

	private Long orderId;

	private Long id;

	public OrderTradeVo() {
	}

	public OrderTradeVo(BigDecimal volume, BigDecimal price) {
		this.volume = volume;
		this.price = price;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
