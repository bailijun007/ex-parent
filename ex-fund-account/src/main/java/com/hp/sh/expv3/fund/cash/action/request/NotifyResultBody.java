/**
 * 
 */
package com.hp.sh.expv3.fund.cash.action.request;

/**
 * @author wangjg
 * 通知支付结果通知
 */
public class NotifyResultBody {

	// 用户id
	private String user_id;
	
	// 币种对应的id
	private String symbol_id;
	
	// 充值订单ID,商户端Cteate回调时返回的
	private String deposit_order_id;
	
	// 签名
	private String sign;
	
	public NotifyResultBody() {
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getSymbol_id() {
		return symbol_id;
	}
	public void setSymbol_id(String symbol_id) {
		this.symbol_id = symbol_id;
	}
	public String getDeposit_order_id() {
		return deposit_order_id;
	}
	public void setDeposit_order_id(String deposit_order_id) {
		this.deposit_order_id = deposit_order_id;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	@Override
	public String toString() {
		return "NotifyResultBody [user_id=" + user_id + ", symbol_id=" + symbol_id + ", deposit_order_id="
				+ deposit_order_id + ", sign=" + sign + "]";
	}

}
