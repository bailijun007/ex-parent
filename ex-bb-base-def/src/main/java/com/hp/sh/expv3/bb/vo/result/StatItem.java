package com.hp.sh.expv3.bb.vo.result;

public class StatItem {
	
	private String name;

	private Long num;

	public StatItem() {
	}

	public StatItem(String name, Long num) {
		this.name = name;
		this.num = num;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "StatItem [name=" + name + ", num=" + num + "]";
	}

}
