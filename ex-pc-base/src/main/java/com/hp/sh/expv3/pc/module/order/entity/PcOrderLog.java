/**
 * @author zw
 * @date 2019/7/19
 */
package com.hp.sh.expv3.pc.module.order.entity;


import com.hp.sh.expv3.base.entity.BaseRecordEntity;

/**
 * 订单日志
 */
public class PcOrderLog extends BaseRecordEntity{

	public static final int TYPE_CREATE = 1;
	public static final int TYPE_CANCEL = 2;
	public static final int TYPE_TRADE = 3;

	public static final int TRIGGER_TYPE_USER = 1;
	public static final int TRIGGER_TYPE_SYS = 2;
	
	private static final long serialVersionUID = 1L;

	private Long orderId;
	
	private Integer type;
	
	private Integer triggerType;

    public PcOrderLog() {
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