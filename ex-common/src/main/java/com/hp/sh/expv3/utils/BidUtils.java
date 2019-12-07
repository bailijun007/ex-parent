package com.hp.sh.expv3.utils;

public class BidUtils {
	
	/**
	 * 1-买，0-卖
	 * @param closeFlag
	 * @param longFlag
	 * @return
	 */
	public static int getBidFlag(int closeFlag, int longFlag){
		if(IntBool.isTrue(closeFlag)){
			return IntBool.reverse(longFlag);
		}else{
			return longFlag;
		}
		
//		return closeFlag^longFlag; //同上
	}

	public static int getLongFlag(int bidFlag, int closeFlag){
		if(IntBool.isTrue(bidFlag)){
			return IntBool.isTrue(closeFlag)?IntBool.NO:IntBool.YES;
		}else{
			return IntBool.isTrue(closeFlag)?IntBool.YES:IntBool.NO;
		}
	}
	
}
