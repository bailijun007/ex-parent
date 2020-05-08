package com.hp.sh.expv3.bb.module.order.entity;

import javax.persistence.Table;

@Table(name="bb_order_history")
public class BBOrderHistory extends BBOrder{
	
	public Long getId() {
		return id;
	}
	
	
}
