package com.hp.sh.expv3.pc.msg;

import javax.persistence.Table;

/**
 * 账户日志
 * @author wangjg
 *
 */
@Table(name="pc_account_log")
public class PcAccountLog {

	public static final int TYPE_TRAD_OPEN_LONG = 1;		//成交开多
	public static final int TYPE_TRAD_OPEN_SHORT = 2;		//成交开空
	public static final int TYPE_TRAD_CLOSE_LONG = 3;		//成交平多
	public static final int TYPE_TRAD_CLOSE_SHORT = 4;		//成交平空
	
	public static final int TYPE_FUND_TO_PC = 5;			//转入
	public static final int TYPE_PC_TO_FUND = 6;			//转出
	
	public static final int TYPE_ADD_TO_MARGIN = 7;			//-手动追加保证金
	public static final int TYPE_REDUCE_MARGIN = 8;			//+减少保证金
	public static final int TYPE_AUTO_ADD_MARGIN = 9;		//-自动追加保证金
	public static final int TYPE_LEVERAGE_ADD_MARGIN = 10;	//-调低杠杆追加保证金
	
	public static final int TYPE_LIQ_LONG = 11;				//强平平多
	public static final int TYPE_LIQ_SHORT = 12;			//强平平空
	
	/**
	 * 日志类型
	 */
	private Integer type;

	//用户Id
	private Long userId;

	//资产
	private String asset;
	//交易品种
	private String symbol;
	//引用对象Id
	private Long refId;
	//时间
	private Long time;
	
	public PcAccountLog() {
	}
	
	public String tags(){
		return this.getType()+"";
	}
	
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAsset() {
		return asset;
	}
	public void setAsset(String asset) {
		this.asset = asset;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public Long getRefId() {
		return refId;
	}
	public void setRefId(Long refId) {
		this.refId = refId;
	}
	public Long getTime() {
		return time;
	}
	public void setTime(Long time) {
		this.time = time;
	}
	
}
