/**
 * @author zw
 * @date 2019/7/24
 */
package com.hp.sh.expv3.component.id.utils;

import java.util.Date;

public class IdBitSetting {

	public static final int dataCenterBits = 2;
	
	public static final int serverBits = 5;
	
	public static final int idTypeBits = 8;
	
	public static final int sequenceBits = 7;

	
	public static void main(String[] args) {
		System.out.println(System.currentTimeMillis());
		System.out.println(System.currentTimeMillis()+1000L*3600*24*365*69);
		
		System.out.println(new Date(System.currentTimeMillis()+1000L*3600*24*365*69).toLocaleString());
	}
}
