package com.hp.sh.expv3.commons.config;

import java.math.BigDecimal;

/**
 * 
 * @author wangjg
 *
 */
public interface SysConfig {
	
	public String getString(String key);
	
	default public Integer getInteger(String key){
		String s = this.getString(key);
		if(s==null||s.isEmpty()){
			return null;
		}
		int n = Integer.parseInt(s);
		return n;
	}
	
	default public Long getLong(String key){
		String s = this.getString(key);
		if(s==null||s.isEmpty()){
			return null;
		}
		long n = Long.parseLong(s);
		return n;
	}
	
	default public BigDecimal getBigDecimal(String key){
		String s = this.getString(key);
		if(s==null||s.isEmpty()){
			return null;
		}
		BigDecimal n = new BigDecimal(s);
		return n;
	}
	
}
