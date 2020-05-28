package com.hp.sh.expv3.commons.date;

import java.util.Calendar;

import com.hp.sh.expv3.constant.ExpTimeZone;

/**
 * 
 * @author wangjg
 *
 */
public class DateFieldUtils {

	public static TimeField getTimeField(Long time){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.setTimeZone(ExpTimeZone.timeZone);
		
		TimeField tf = new TimeField();
		
		tf.setYear(cal.get(Calendar.YEAR));
		tf.setMonth(cal.get(Calendar.MONTH)+1);
		tf.setDay(cal.get(Calendar.DAY_OF_MONTH));
		tf.setHour(cal.get(Calendar.HOUR_OF_DAY));
		tf.setMinute(cal.get(Calendar.MINUTE));
		tf.setSecond(cal.get(Calendar.SECOND));
		
		return tf;
	}
	

}
