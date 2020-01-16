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
		return "TS"+genTimeSn();
	}

	public static String genRecordSn(){
		return "RC"+genTimeSn();
	}
	
	public static String genDepositSn(){
		return "DP"+genTimeSn();
	}
	
	public static String genWithDrawSn(){
		return "WD"+genTimeSn();
	}

	//c2c
	public static String genPLPayInSn(){
		return "PLIN"+genTimeSn();
	}
	
	public static String genPLPayOutSn(){
		return "PLOUT"+genTimeSn();
	}
	
	//Synch
	public static String genSynchAddSn(String sn){
		return "A-"+genTimeSn();
	}

	public static String genSynchCutSn(String sn){
		return "C-"+genTimeSn();
	}

	public static String genSynchReturnSn(String sn){
		return "R-"+genTimeSn();
	}
	
	public static String genRndSn(){
		return genRndSn("");
	}
	
	public static String genRndSn(String prefix){
		String dstr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
		return "RND+prefix"+dstr;
	}

	static String genTimeSn(){
		String dstr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
		String nstr = RandomStringUtils.randomNumeric(4);
		return dstr+nstr;
	}

}
