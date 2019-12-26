package com.hp.sh.expv3.pc.msg;

/**
 * 事件消息
 * @author wangjg
 */
public class EventMsg {

	public static final int TYPE_PC_ACCOUNT = 1;
	public static final int TYPE_ORDER = 2;
	public static final int TYPE_POS = 3;
	
	/**
	 * 事件类型
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
	
	public EventMsg() {
	}
	
	public EventMsg(Integer type, Long refId, Long time, Long userId, String asset, String symbol) {
		this.type = type;
		this.userId = userId;
		this.asset = asset;
		this.symbol = symbol;
		this.refId = refId;
		this.time = time;
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
