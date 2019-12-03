package com.hp.sh.expv3.pc.module.order.constant;

public class IntBool {

	public static final int NO = 0;

	public static final int YES = 1;
	
	public static final boolean isTrue(int value){
		return value == YES;
	} 
	
	//取反
	public static final int not(int value){
		return isTrue(value)?NO:YES;
	} 

}
