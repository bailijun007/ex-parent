package com.hp.sh.expv3.utils.math;

/**
 * 
 * @author wangjg
 *
 */
public class NumberUtils {
	
	//是否负数
	public static boolean in(int n, int...vals){
		for(int i : vals){
			if(i==n){
				return true;
			}
		}
		return false;
	}

	
}
