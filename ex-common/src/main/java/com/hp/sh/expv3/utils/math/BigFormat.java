package com.hp.sh.expv3.utils.math;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigFormat {
	
	public static String plain(BigDecimal num){
		return num.stripTrailingZeros().toPlainString();
	}
	
	public static String format(String str, Object...num){
		Object[] sa = new Object[num.length];
		for(int i=0; i<num.length; i++){
			Object n = num[i];
			if(n instanceof BigDecimal){
				sa[i] = plain((BigDecimal)n);
			}else{
				sa[i] = n;
			}
		}
		return String.format(str, sa);
	}
	
	public static String s(BigDecimal num){
		return new DecimalFormat("0.####").format(num);
	}
	
	public static void main(String[] args) {
		BigDecimal pi = new BigDecimal(3.1415927);
		System.out.println(BigFormat.s(pi));
		System.out.println(BigFormat.format("%s", pi));
	}

}
