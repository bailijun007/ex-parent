package com.hp.sh.expv3.component.dbshard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateFormatUtils;

import com.hp.sh.expv3.constant.ExpTimeZone;

public class DateShardUtils {
	private static final String dateFormat = "yyyyMM";

	public static List<String> getRangeDates(Long start, Long end) {
		Calendar endCal = Calendar.getInstance();
		endCal.setTimeInMillis(end);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(start);

		String startStr = getDateShard(start);
		String endStr = getDateShard(end);
		List<String> list = new ArrayList<String>();
		list.add(startStr);
		
		while(true){
			cal.add(Calendar.MONTH, 1);
			String s = getDateShard(cal);
			if(s.compareTo(endStr) <= 0){
				list.add(s);
			}else{
				break;
			}
		}
		
		return list;
	}

	public static String getDateShard(Object value){
		if(value instanceof String){
			return value.toString().substring(dateFormat.length());
		}else if(value instanceof Calendar){
			return formatTableDate((Calendar)value);
		}
		
		Date date = null;
		
		if(value instanceof Long){
			date = new Date((Long)value);
		}else{
			date = (Date)value;
		}
		
		String dateStr = formatTableDate(date);
		
		return dateStr;
	}

	public static Date getTableDate(String tableName) {
		int pos = tableName.lastIndexOf('_');
		String dateStr = tableName.substring(pos+1);
		Date date = parseTableDate(dateStr);
		return date;
	}
	
	static String formatTableDate(Calendar calendar){
		String dateStr = DateFormatUtils.format(calendar, dateFormat, ExpTimeZone.timeZone);
		return dateStr;
	}
	
	static String formatTableDate(Date date){
		SimpleDateFormat df = dateFormat();
		String dateStr = df.format(date);
		return dateStr;
	}
	
	static Date parseTableDate(String dateStr){
		
		SimpleDateFormat df = dateFormat();
		try {
			Date date = df.parse(dateStr);
			return date;
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	private static SimpleDateFormat dateFormat(){
		SimpleDateFormat df = new SimpleDateFormat(dateFormat);
		df.setTimeZone(ExpTimeZone.timeZone);
		return df;
	}

	public static void main(String[] args) {
		Long now = System.currentTimeMillis();
		System.out.println(DateShardUtils.getRangeDates(1588262400000L, 1588262400000L));
	}
	
}
