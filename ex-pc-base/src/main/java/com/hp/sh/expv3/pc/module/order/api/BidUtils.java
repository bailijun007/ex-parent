package com.hp.sh.expv3.pc.module.order.api;

import com.hp.sh.expv3.utils.IntBool;

public class BidUtils {
	
	/**
	 * 1-买，0-卖
	 * @param closeFlag
	 * @param longFlag
	 * @return
	 */
	public static int getBidFlag(int closeFlag, int longFlag){
		if(IntBool.isTrue(closeFlag)){
			return IntBool.not(longFlag);
		}else{
			return longFlag;
		}
	}

}
