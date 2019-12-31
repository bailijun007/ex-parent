/**
 * 
 */
package com.hp.sh.expv3.utils;

import java.util.Date;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 */
public class SnUtils {
	
	public static String genTransferSn(){
		return "TS"+genSn();
	}
	
	public static String genRecordSn(){
		return "RC"+genSn();
	}
	
	public static String genDepositSn(){
		String dstr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		return "DP"+dstr;
	}
	
	public static String genWithDrawSn(){
		String dstr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		return "WD"+dstr;
	}
	
	public static String genRndSn(){
		return genRndSn("");
	}
	public static String genRndSn(String prefix){
		String dstr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		return "RND+prefix"+dstr;
	}
	
	static String genSn(){
		String dstr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
		String nstr = RandomStringUtils.randomNumeric(4);
		return dstr+nstr;
	}

}
