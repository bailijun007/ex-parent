package com.hp.sh.expv3.utils;

/**
 * 整数表示的布尔值
 * @author wangjg
 *
 */
public class IntBool {

	public static final int NO = 0;

	public static final int YES = 1;
	
	public static final boolean isTrue(int value){
		check(value);
		return value == YES;
	}
	
	public static final boolean isFlase(int value){
		check(value);
		return value != YES;
	} 
	
	//取反
	public static final int reverse(int value){
		return isTrue(value)?NO:YES;
//		return 0^value;
	}

	private static void check(int value) {
		if(value!=0 && value!=1){
			throw new RuntimeException("错误的IntBool值："+value);
		}
	} 

}
