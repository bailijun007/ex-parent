package com.hp.sh.expv3.commons.date;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hp.sh.expv3.constant.ExpTimeZone;

/**
 * 
 * @author wangjg
 *
 */
public class DateChunkUtils {
	private static final String dateFormat = "yyyyMMdd";
	
	public static String getHour(Long time){
		SimpleDateFormat df = dateFormat();
		String dateStr = df.format(new Date(time));

		TimeField df1 = DateFieldUtils.getTimeField(time);
		return dateStr+"_"+df1.getHour();
	}
	
	public static String getHour4x(Long time){
		SimpleDateFormat df = dateFormat();
		String dateStr = df.format(new Date(time));

		TimeField df1 = DateFieldUtils.getTimeField(time);
		int hours = df1.getHour()/6;
		return dateStr+"_"+hours;
	}
	
	private static SimpleDateFormat dateFormat(){
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setTimeZone(ExpTimeZone.timeZone);
		return df;
	}

}
