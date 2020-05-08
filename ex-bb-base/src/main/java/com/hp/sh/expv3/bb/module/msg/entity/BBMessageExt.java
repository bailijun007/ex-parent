package com.hp.sh.expv3.bb.module.msg.entity;

import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.UserData;

/**
 * 币币_撮合消息
 * @author lw
 *
 */
@Table(name="bb_message_ext")
public class BBMessageExt implements UserData{
	
	public static final int STATUS_NEW = 0;

	public static final int STATUS_RETRY = 2;
	
	public static final int STATUS_ERR = 3;

	public static final int STATUS_FINISHED = 4;
	
	private Long id;
	
	//msgId
	private String msgId;

	private String tags;

	private String keys;
	
	private String msgBody;
	
	//异常信息
	private String errorInfo;
	
	//用户ID
	private Long userId;
	
	//资产
	protected String asset;
	
	//交易对（合约品种）
	protected String symbol;
	
	// 创建时间
	private Long created;
	
	//分片Id
	private Long shardId;
	
	private int status;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BBMessageExt() {
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String id) {
		this.msgId = id;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tag) {
		this.tags = tag;
	}

	public String getKeys() {
		return keys;
	}

	public void setKeys(String key) {
		this.keys = key;
	}

	public String getMsgBody() {
		return msgBody;
	}

	public void setMsgBody(String body) {
		this.msgBody = body;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getCreated() {
		return created;
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public void setErrorInfo(String exMessage) {
		this.errorInfo = exMessage;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getShardId() {
		return shardId;
	}

	public void setShardId(Long shardId) {
		this.shardId = shardId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BBMessageExt other = (BBMessageExt) obj;
		if (msgId == null) {
			if (other.msgId != null)
				return false;
		} else if (!msgId.equals(other.msgId))
			return false;
		return true;
	}

}
