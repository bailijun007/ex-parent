package com.hp.sh.expv3.pc.module.position.entity;

import javax.persistence.Table;

@Table(name="pc_position_history")
public class PcPositionHistory extends PcPosition{
	
	private static final long serialVersionUID = 1L;

	public Long getId() {
		return id;
	}
	
}
