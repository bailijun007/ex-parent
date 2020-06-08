package com.hp.sh.expv3.utils;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.hp.sh.expv3.commons.exception.ExSysException;
import com.hp.sh.expv3.error.ExCommonError;
import com.hp.sh.expv3.utils.math.BigUtils;

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
	
	public static void checkPositiveNum(Number...params) {
		for(Number param : params){
			if(param==null){
				throw new ExSysException(ExCommonError.PARAM_EMPTY);
			}else {
				if(param instanceof BigDecimal){
					if(BigUtils.leZero((BigDecimal)param)){
						throw new ExSysException(ExCommonError.REQUIRE_POSITIVE_PARAM, param);
					}
				}else{
					if(param.doubleValue()<=0){
						throw new ExSysException(ExCommonError.REQUIRE_POSITIVE_PARAM, param);
					}
				}
			}
		}
	}
	
	public static void checkIntBool(Integer...params) {
		for(Integer param : params){
			if(param==null){
				throw new ExSysException(ExCommonError.PARAM_EMPTY);
			}else {
				if(!IntBool.isBool(param)){
					throw new ExSysException(ExCommonError.REQUIRE_BOOL);
				}
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void checkRange(Comparable small, Comparable bigger) {
		if(small.compareTo(bigger) > 0){
			throw new ExSysException(ExCommonError.PARAM_RANGE_ERROR);
		}
	}
	
}
