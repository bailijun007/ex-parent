/**
 * 
 */
package com.hp.sh.expv3.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author wangjg
 */
public class StringConvert {
	
	public static List<Long> toIdList(String ids){
		if(ids==null || ids.isEmpty()){
			return Collections.emptyList();
		}
		String[] sa = ids.split(",");
		List<Long> idList = new ArrayList<Long>();
		for(String s : sa){
			idList.add(Long.parseLong(s));
		}
		return idList;
	}

}
