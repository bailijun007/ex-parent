package com.hp.sh.expv3.bb.module.order.vo;

public class Action {

	/*
	 * @see PcOrderLogType#*
	 * 动作
	 */
	private Integer optType;
	
	/* @see TriggerType#*
	 * 触发类型
	 */
	private Integer triggerType;
	
	private long time;

	public Action() {
		super();
	}

	public Action(Integer optType, Integer triggerType) {
		this.optType = optType;
		this.triggerType = triggerType;
	}

	public Action(Integer optType, Integer triggerType, long time) {
		super();
		this.optType = optType;
		this.triggerType = triggerType;
		this.time = time;
	}

	public Integer getOptType() {
		return optType;
	}

	public void setOptType(Integer optType) {
		this.optType = optType;
	}

	public Integer getTriggerType() {
		return triggerType;
	}

	public void setTriggerType(Integer triggerType) {
		this.triggerType = triggerType;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	
}
