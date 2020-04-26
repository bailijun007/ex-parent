package com.hp.sh.expv3.bb.module.msg.entity;


import javax.persistence.Table;

/**
 * 消息偏移量(系统表)
 */
@Table(name="bb_message_offset")
public class BBMessageOffset {

	//分片Id
	private Integer shardId;
	
	//读取位置
	private Long readedOffset;
	
	// 修改时间
	private Long modified;

    public BBMessageOffset() {
	}

	public Integer getShardId() {
		return shardId;
	}

	public void setShardId(Integer shardId) {
		this.shardId = shardId;
	}

	public Long getReadedOffset() {
		return readedOffset;
	}

	public void setReadedOffset(Long offsetId) {
		this.readedOffset = offsetId;
	}

	public Long getModified() {
		return modified;
	}

	public void setModified(Long modified) {
		this.modified = modified;
	}

}