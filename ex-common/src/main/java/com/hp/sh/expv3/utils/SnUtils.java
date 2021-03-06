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

	public static String newTransferSn(){
		return "TS"+newTimeSn();
	}

	public static String newRecordSn(){
		return "RC"+newTimeSn();
	}
	
	public static String newDepositSn(){
		return "DP"+newTimeSn();
	}
	
	public static String newWithDrawSn(){
		return "WD"+newTimeSn();
	}

	//c2c
	public static String newPLPayInSn(){
		return "PLIN"+newTimeSn();
	}
	
	public static String newPLPayOutSn(){
		return "PLOUT"+newTimeSn();
	}
	
	//Synch
	public static String getSynchAddSn(String sn){
		return "A"+sn;
	}

	public static String getSynchCutSn(String sn){
		return "C"+sn;
	}

	public static String getSynchReturnSn(String sn){
		return "R"+sn;
	}
	
	//order
	public static String getOrderPaySn(String sn){
		return "O"+sn;
	}
	public static String getCancelOrderReturnSn(String sn){
		return "CO"+sn;
	}

	static String newTimeSn(){
		String dstr = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
		String nstr = RandomStringUtils.randomNumeric(7);
		return dstr+nstr;
	}

	public static String getSellIncomeSn(Long orderTradeId) {
		return "SI"+orderTradeId;
	}
	
	public static String getBuyInSn(Long orderTradeId) {
		return "BI"+orderTradeId;
	}
	
	public static String getRemainSn(Long orderId) {
		return "R"+orderId;
	}

	public static String getReleaseSn(Long orderId) {
		return "RL"+orderId;
	}
	
	public static Long getId(String sn) {
//		String str = sn.replaceAll("[^0-9]+", "");
		String str = sn.replaceAll("[A-Z]+", "");
		return Long.parseLong(str);
	}

}
