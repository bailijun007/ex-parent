package com.hp.sh.expv3.component.dbshard.impl;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.hp.sh.expv3.component.dbshard.IdDateShard;
import com.hp.sh.expv3.component.id.utils.IdBitSetting;
import com.hp.sh.expv3.component.id.utils.SnowflakeIdWorker;
import com.hp.sh.expv3.constant.ExpTimeZone;

public class TradeId2DateShard implements IdDateShard{
	
	private SnowflakeIdWorker snowflakeIdWorker;
	
	public TradeId2DateShard() {
	}

	@Override
	public String getDateShardById(Object id) {
		Long _id = (Long)id;
		int type = snowflakeIdWorker.getIdType(_id);

		YearMonth ym = getYearMonth(SnowflakeIdWorker.twepoch);
		ym.addMonths(type);
		
		return ym.toString();
	}
	
	public long genTradId(long tradeTime){
		int type = this.getTypeByTime(tradeTime);
		long id = this.snowflakeIdWorker.nextId();
		id = snowflakeIdWorker.resetIdType(id, type);
		return id;
	}
	
	private int getTypeByTime(long time){
		YearMonth ym1 = this.getYearMonth(SnowflakeIdWorker.twepoch);
		
		YearMonth ym2 = this.getYearMonth(time);
		
		int months = (ym2.year*12+ym2.month) - (ym1.year*12+ym1.month);
		
		return months;
	}
	
	public void setSnowflakeIdWorker(SnowflakeIdWorker snowflakeIdWorker) {
		this.snowflakeIdWorker = snowflakeIdWorker;
	}

	private YearMonth getYearMonth(Long time){
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		cal.setTimeZone(ExpTimeZone.timeZone);
		
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH)+1;
		return new YearMonth(year, month);
	}
	
	public static class YearMonth{
		int year;
		int month;
		
		public YearMonth(int year, int month) {
			this.year = year;
			this.month = month;
		}
		
		public void addMonths(int months){
			int m = this.month+months;
			this.year += m/12;
			this.month = m%12+1;
		}

		@Override
		public String toString() {
			return year+StringUtils.leftPad(String.valueOf(month), 2, '0');
		}
	}
	
	public static void main(String[] args) {
		TradeId2DateShard tid = new TradeId2DateShard();
		SnowflakeIdWorker idw=new SnowflakeIdWorker(IdBitSetting.dataCenterBits, IdBitSetting.serverBits, IdBitSetting.idTypeBits, IdBitSetting.sequenceBits);
		tid.setSnowflakeIdWorker(idw);
		long newId = tid.genTradId(1589255046516L);
		System.out.println(newId);
		
		System.out.println(idw.getIdType(newId));
		System.out.println(idw.getTime(newId));
		System.out.println(new Date(idw.getTime(newId)).toLocaleString());
	}

}
