package com.hp.sh.expv3.utils;

import org.apache.commons.lang3.StringUtils;

import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.error.ExCommonError;

public class CheckUtils {

	public static void checkRequired(Object...params) {
		for(Object param : params){
			if(param==null){
				throw new ExSysException(ExCommonError.PARAM_EMPTY);
			}else if(param instanceof String){
				if(StringUtils.isBlank((String)param)){
					throw new ExSysException(ExCommonError.PARAM_EMPTY);
				}
			}
		}
	}
	
}
