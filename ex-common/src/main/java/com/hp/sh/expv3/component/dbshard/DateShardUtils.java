package com.hp.sh.expv3.component.dbshard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

public class DateShardUtils {
	private static final String dateFormat = "yyyyMM";

	public static List<String> getRangeDates(Long start, Long end) {
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(end);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(start);

		String startStr = getDate(start);
		String endStr = getDate(end);
		List<String> list = new ArrayList<String>();
		list.add(startStr);
		
		while(true){
			cal.add(Calendar.MONTH, 1);
			String s = getDate(cal);
			if(s.compareTo(endStr) <= 0){
				list.add(s);
			}else{
				break;
			}
		}
		
		return list;
	}

	public static String getDate(Object value){
		if(value instanceof String){
			return value.toString().substring(dateFormat.length());
		}else if(value instanceof Calendar){
			return DateFormatUtils.format((Calendar)value, dateFormat);
		}
		
		Date date = null;
		
		if(value instanceof Long){
			date = new Date((Long)value);
		}else{
			date = (Date)value;
		}
		String str = DateFormatUtils.format(date, dateFormat);
		return str;
	}
	
	public static void main(String[] args) {
		Long now = System.currentTimeMillis();
		System.out.println(DateShardUtils.getRangeDates(now, now));
	}
	
}
