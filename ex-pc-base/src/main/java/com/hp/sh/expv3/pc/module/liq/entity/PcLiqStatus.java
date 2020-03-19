package com.hp.sh.expv3.pc.module.liq.entity;

/**
 * 强平状态
 * @author wangjg
 *
 */
public class PcLiqStatus {

	public static final int init = 0;
	
	//破产价委托
	public static final int step1 = 1;
	
	//破产价委托失败，低于破产价委托
	public static final int step2 = 2;

	//低于破产价委托失败，自动减仓
	public static final int step3 = 3;
	
	//结束
	public static final int finished = 4;

}
