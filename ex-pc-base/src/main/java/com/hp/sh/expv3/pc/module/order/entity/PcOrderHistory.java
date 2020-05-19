package com.hp.sh.expv3.pc.module.order.entity;

import javax.persistence.Table;

@Table(name="pc_order_history")
public class PcOrderHistory extends PcOrder{
	
	public Long getId() {
		return id;
	}
	
	
}
