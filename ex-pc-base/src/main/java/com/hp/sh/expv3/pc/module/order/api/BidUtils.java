package com.hp.sh.expv3.pc.module.order.api;

import com.hp.sh.expv3.pc.module.order.constant.IntBool;

public class BidUtils {
	
	public static int getBidFlag(int closeFlag, int longFlag){
		if(IntBool.isTrue(closeFlag)){
			return IntBool.not(longFlag);
		}else{
			return longFlag;
		}
	}

}
