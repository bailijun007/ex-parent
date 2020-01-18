/**
 * 
 */
package com.hp.sh.expv3.fund.cash.action.request;

/**
 * @author wangjg
 * 通知创建支付参数
 */
public class NotifyCreateBody {

	//用户id
	private String user_id;
	//币种对应的id
	private String symbol_id;
	//交互链的订单id,唯一
	private String chain_order_id;
	//哈希值
	private String tx_hash;
	//划转数量
	private String volume;
	
	//币充入的地址，即用户的充币地址
	private String address;
	//充币时间
	private String deposit_time;
	//签名
	private String sign;
	
	public NotifyCreateBody() {
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

	public String getChain_order_id() {
		return chain_order_id;
	}

	public void setChain_order_id(String chain_order_id) {
		this.chain_order_id = chain_order_id;
	}

	public String getTx_hash() {
		return tx_hash;
	}

	public void setTx_hash(String tx_hash) {
		this.tx_hash = tx_hash;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDeposit_time() {
		return deposit_time;
	}

	public void setDeposit_time(String deposit_time) {
		this.deposit_time = deposit_time;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
