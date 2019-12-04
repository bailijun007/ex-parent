package com.hp.sh.expv3.constant;

/**
 * 幂等接口，执行结果
 * @author lw
 *
 */
public class InvokeResult {
	
	//成功
	public static final int SUCCESS = 1;

	//已经执行过了,这一次什么也没改变
	public static final int NOCHANGE = 2;

	//失败
	public static final int FAIL= 3;	

}
