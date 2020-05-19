
package com.hp.sh.expv3.pc.module.msg.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.config.shard.ShardGroup;
import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.module.msg.dao.PcMessageExtDAO;
import com.hp.sh.expv3.pc.module.msg.entity.PcMessageExt;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcNotMatchedMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcCancelledMsg;
import com.hp.sh.expv3.pc.mq.consumer.msg.PcTradeMsg;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcMessageExtService{
	
    /** 起始时间戳 */
    private final long twepoch = 1587571200000L;

	@Autowired
	private PcMessageExtDAO messageExtDAO;
	
	@Autowired
	private ShardGroup shardGroup;
	
	public void delete(Long userId, Long id){
		this.messageExtDAO.delete(userId, id);
	}
	
	public PcMessageExt saveNotMatchedMsg(String tags, PcNotMatchedMsg msg){
		PcMessageExt msgEntity = new PcMessageExt();
		msgEntity.setUserId(msg.getAccountId());
		
		msgEntity.setAsset(msg.getAsset());
		msgEntity.setSymbol(msg.getSymbol());
		msgEntity.setErrorInfo(null);
		
		msgEntity.setMsgId(msg.getMsgId());
		msgEntity.setTags(tags);
		msgEntity.setKeys(""+msg.getOrderId());
		msgEntity.setMsgBody(JsonUtils.toJson(msg));
		
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setShardId(getMsgSardId(msgEntity));
		msgEntity.setStatus(PcMessageExt.STATUS_NEW);
		
		msgEntity.setId(msg.getSeqId());
		
		this.messageExtDAO.save(msgEntity);
		
		return msgEntity;
	}

	public PcMessageExt saveTradeMsg(String tags, PcTradeMsg msg, String exMessage) {
		exMessage = this.cutExMsg(exMessage);
		
		PcMessageExt msgEntity = new PcMessageExt();
		msgEntity.setUserId(msg.getAccountId());
		
		msgEntity.setAsset(msg.getAsset());
		msgEntity.setSymbol(msg.getSymbol());
		msgEntity.setErrorInfo(exMessage);
		
		msgEntity.setMsgId(msg.getMsgId());
		msgEntity.setTags(tags);
		msgEntity.setKeys(""+msg.getOrderId());
		msgEntity.setMsgBody(JsonUtils.toJson(msg));
		
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setShardId(getMsgSardId(msgEntity));
		msgEntity.setStatus(PcMessageExt.STATUS_NEW);
		
		msgEntity.setId(msg.getSeqId());
		
		this.messageExtDAO.save(msgEntity);
		
		return msgEntity;
	}

	public PcMessageExt saveCancelIfNotExists(String tag, PcCancelledMsg msg, String exMessage) {
		
		exMessage = this.cutExMsg(exMessage);
		
		boolean existCancelledMsg = this.exist(msg.getAccountId(), tag, ""+msg.getOrderId());
		if(existCancelledMsg){
			return null;
		}
		
		PcMessageExt msgEntity = new PcMessageExt();
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setUserId(msg.getAccountId());
		msgEntity.setAsset(msg.getAsset());
		msgEntity.setSymbol(msg.getSymbol());
		msgEntity.setErrorInfo(exMessage);
		msgEntity.setTags(tag);
		msgEntity.setMsgId(msg.getMsgId());
		msgEntity.setKeys(""+msg.getOrderId());
		msgEntity.setMsgBody(JsonUtils.toJson(msg));
		msgEntity.setShardId(getMsgSardId(msgEntity));
		msgEntity.setStatus(PcMessageExt.STATUS_NEW);
		
		msgEntity.setId(msg.getSeqId());
		
		this.messageExtDAO.save(msgEntity);
		
		return msgEntity;
	}
	
	public void setStatus(Long userId, Long id, int status, String errInfo) {
		this.messageExtDAO.setStatus(userId, id, status, this.cutExMsg(errInfo));
	}

	@LockIt(key="mm-${userId}-${key}")
	public boolean exist(Long userId, String tags, String keys){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
		params.put("asc", true);

		params.put("userId", userId);
		params.put("tags", tags);
		params.put("keys", keys);
		
		PcMessageExt msg = this.messageExtDAO.queryOne(params);
		return msg!=null;
	}
	
	public boolean existMsgId(Long userId, String msgId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
		params.put("asc", true);

		params.put("userId", userId);
		params.put("msgId", msgId);
		
		PcMessageExt msg = this.messageExtDAO.queryOne(params);
		return msg!=null;
	}

	public PcMessageExt findFirst(String tag, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
//		params.put("status", PcMessageExt.STATUS_NEW);
		params.put("orderBy", "id");
		params.put("asc", true);
		params.put("tags", tag);
		params.put("symbol", symbol);
		PcMessageExt msg = this.messageExtDAO.queryOne(params);
		return msg;
	}

	public List<PcMessageExt> findFirstList(int pageSize, Long shardId, Long startId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, pageSize, 10000L));
		params.put("shardId", shardId);
		params.put("startId", startId);
//		params.put("status", PcMessageExt.STATUS_NEW);
		params.put("orderBy", "id");
		params.put("asc", true);
		List<PcMessageExt> msgList = this.messageExtDAO.queryList(params);
		return msgList;
	}
	
	private Long getMsgSardId(PcMessageExt msgEntity){
		return shardGroup.getMsgSardId(msgEntity.getUserId());
	}
	
	private String cutExMsg(String exMessage){
		if(exMessage!=null && exMessage.length()>1500){
			exMessage = exMessage.substring(0, 1500);
		}
		return exMessage;
	}

	private Long getSortId(String tag, Long created) {
		long time = created - twepoch;
		long tagbit = 1L<<60;
		if(tag.equals(MqTags.TAGS_TRADE)){
			tagbit = 0;
		}
		long sortId = tagbit | time;
		return sortId;
	}
	
}
