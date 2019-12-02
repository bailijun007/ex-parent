/**
 * 
 */
package com.hp.sh.expv3.constant;

/**
 * @author wangjg
 */
public class PayChannel {
	
	//支付宝
	public static final int ALIPAY = PayChannelEnum.ALIPAY.getCode();
	
	//微信
	public static final int WEIXIN_PAY = PayChannelEnum.WEIXIN_PAY.getCode();
	
	//BYS
	public static final int BYS = PayChannelEnum.BYS.getCode();

	public static String getName(int id){
		return PayChannelEnum.code2Enum.get(id).getName();
	}

}
