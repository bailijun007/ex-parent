/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.pc.module.order.entity;


import com.hp.sh.expv3.base.entity.BaseAccountRecordEntity;

/**
 * 订单日志
 */
public class PcOrderLog extends BaseAccountRecordEntity{

	public static final int TYPE_CREATE = 1;
	public static final int TYPE_CANCEL = 2;

	public static final int TRIGGER_TYPE_USER = 1;
	public static final int TRIGGER_TYPE_SYS = 2;
	
	private static final long serialVersionUID = 1L;

	private Long userId;
	
	private Long orderId;
	
	private Integer type;
	
	private Integer triggerType;

    public PcOrderLog() {
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

}