/**
 * 
 */
package com.hp.sh.expv3.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.CaseFormat;

/**
 * 
 */
public class StringUtils {
	
	public static String toCamel(String str){
		return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, str);
	}
	
	public static String toUnderscore(String str){
		return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, str);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map keyToCamel(Map map){
		Map newMap = new HashMap();
		Set<Entry<String, Object>> entrySet = map.entrySet();
		for(Entry<String, Object> entry : entrySet){
			newMap.put(toCamel(entry.getKey()), entry.getValue());
		}
		return newMap;
	}
	
	public static void main(String[] args) {
		System.out.println(StringUtils.toCamel("ab_bc_cde"));
		System.out.println(StringUtils.toUnderscore("abBcCde"));
	}

}
