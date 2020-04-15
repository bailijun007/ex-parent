package com.hp.sh.expv3.bb.module.fail.entity;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.hp.sh.expv3.base.entity.UserData;
import com.hp.sh.expv3.component.id.utils.GeneratorName;

/**
 * 币币_消息
 * @author lw
 *
 */
@Table(name="bb_mq_msg")
public class BBMqMsg implements UserData{
	
	private Long id;

	//messageId
	private String messageId;

	private String tag;

	private String key;
	
	private String body;
	
	//处理方法
	private String method;
	
	//用户ID
	private Long userId;

	// 创建时间
	private Long created;
	
	public BBMqMsg() {
	}

	@Id
	@GeneratedValue(generator=GeneratorName.SNOWFLAKE)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String id) {
		this.messageId = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
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

	@Override
	public String toString() {
		return "BBMqMsg [id=" + messageId + ", method=" + method + ", tag=" + tag + ", key=" + key + ", body=" + body
				+ ", userId=" + userId + ", created=" + created + "]";
	}

}
