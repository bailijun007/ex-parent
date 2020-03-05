/**
 * 
 */
package com.hp.sh.expv3.fund.constant;

/**
 * @author wangjg
 */
public class PayChannel {
	
	public static final int ALIPAY = PayChannelEnum.ALIPAY.getCode();
	
	public static final int WEIXIN_PAY = PayChannelEnum.WEIXIN_PAY.getCode();
	
	public static final int BYS = PayChannelEnum.BYS.getCode();

	public static String getName(int id){
		return PayChannelEnum.code2Enum.get(id).getName();
	}

}
