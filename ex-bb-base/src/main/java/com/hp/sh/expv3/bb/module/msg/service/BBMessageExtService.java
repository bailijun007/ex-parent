
package com.hp.sh.expv3.bb.module.msg.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.msg.dao.BBMessageExtDAO;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageExt;
import com.hp.sh.expv3.bb.mq.msg.in.BBNotMatchMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BBCancelledMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.config.shard.ShardGroup;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBMessageExtService{
	
    /** 起始时间戳 */
    private final long twepoch = 1587571200000L;

	@Autowired
	private BBMessageExtDAO messageExtDAO;
	
	@Autowired
	private ShardGroup shardGroup;

	public void delete(Long userId, Long id){
		this.messageExtDAO.delete(userId, id);
	}
	
	public BBMessageExt saveNotMatchedMsg(String tags, BBNotMatchMsg msg){
		BBMessageExt msgEntity = new BBMessageExt();
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
		msgEntity.setStatus(BBMessageExt.STATUS_NEW);
		
		msgEntity.setId(msg.getSeqId());
		
		this.messageExtDAO.save(msgEntity);
		
		return msgEntity;
	}

	public BBMessageExt saveTradeMsg(String tags, BBTradeVo msg, String exMessage) {
		exMessage = this.cutExMsg(exMessage);
		
		BBMessageExt msgEntity = new BBMessageExt();
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
		msgEntity.setStatus(BBMessageExt.STATUS_NEW);
		
		msgEntity.setId(msg.getSeqId());
		
		this.messageExtDAO.save(msgEntity);
		
		return msgEntity;
	}

	public BBMessageExt saveCancelIfNotExists(String tag, BBCancelledMsg msg, String exMessage) {
		
		exMessage = this.cutExMsg(exMessage);
		
		boolean existCancelledMsg = this.exist(msg.getAccountId(), tag, ""+msg.getOrderId());
		if(existCancelledMsg){
			return null;
		}
		
		BBMessageExt msgEntity = new BBMessageExt();
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
		msgEntity.setStatus(BBMessageExt.STATUS_NEW);
		
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
		
		BBMessageExt msg = this.messageExtDAO.queryOne(params);
		return msg!=null;
	}
	
	public boolean existMsgId(Long userId, String msgId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
		params.put("asc", true);

		params.put("userId", userId);
		params.put("msgId", msgId);
		
		BBMessageExt msg = this.messageExtDAO.queryOne(params);
		return msg!=null;
	}

	public BBMessageExt findFirst(String tag, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
//		params.put("status", BBMessageExt.STATUS_NEW);
		params.put("orderBy", "id");
		params.put("asc", true);
		params.put("tags", tag);
		params.put("symbol", symbol);
		BBMessageExt msg = this.messageExtDAO.queryOne(params);
		return msg;
	}

	public List<BBMessageExt> findFirstList(int pageSize, Long shardId, Long startId){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, pageSize, 10000L));
		params.put("shardId", shardId);
		params.put("startId", startId);
//		params.put("status", BBMessageExt.STATUS_NEW);
		params.put("orderBy", "id");
		params.put("asc", true);
		List<BBMessageExt> msgList = this.messageExtDAO.queryList(params);
		return msgList;
	}
	
	private Long getMsgSardId(BBMessageExt msgEntity){
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
