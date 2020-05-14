package com.hp.sh.expv3.pc.mq.consumer.msg;

import java.math.BigDecimal;

/**
 * 新订单消息
 * @author lw
 *
 */
public class OrderPendingNewMsg extends BaseSymbolMsg{

	private Long orderId;
	private Long accountId;
	private Integer closeFlag;
	private Integer bidFlag;

	private BigDecimal number;
	private BigDecimal displayNumber;
	private BigDecimal price;
	private Integer orderType;
	private Long orderTime;
	
	private Integer timeInForce;
	
	
	public OrderPendingNewMsg() {
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public Integer getCloseFlag() {
		return closeFlag;
	}

	public void setCloseFlag(Integer closeFlag) {
		this.closeFlag = closeFlag;
	}

	public Integer getBidFlag() {
		return bidFlag;
	}

	public void setBidFlag(Integer bidFlag) {
		this.bidFlag = bidFlag;
	}

	public BigDecimal getNumber() {
		return number;
	}

	public void setNumber(BigDecimal number) {
		this.number = number;
	}

	public BigDecimal getDisplayNumber() {
		return displayNumber;
	}

	public void setDisplayNumber(BigDecimal displayNumber) {
		this.displayNumber = displayNumber;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Long getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Long orderTime) {
		this.orderTime = orderTime;
	}

	public Integer getTimeInForce() {
		return timeInForce;
	}

	public void setTimeInForce(Integer timeInForce) {
		this.timeInForce = timeInForce;
	}

	@Override
	public String toString() {
		return "OrderPendingNewMsg [orderId=" + orderId + ", accountId=" + accountId + ", closeFlag=" + closeFlag
				+ ", bidFlag=" + bidFlag + ", number=" + number + ", displayNumber=" + displayNumber + ", price="
				+ price + ", orderType=" + orderType + ", orderTime=" + orderTime + ", asset=" + asset + ", symbol="
				+ symbol + "]";
	}
	
}
