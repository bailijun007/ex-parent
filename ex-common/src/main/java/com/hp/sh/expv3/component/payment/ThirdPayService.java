/**
 * 
 */
package com.hp.sh.expv3.component.payment;

import java.math.BigDecimal;

/**
 * @author wangjg
 */
public interface ThirdPayService {
	
	public <T> T getPayParams(String account, BigDecimal amount);
	
	public int queryStatus(String sn);
	
	public String getName();

}
