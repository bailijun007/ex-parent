
package com.hp.sh.expv3.bb.module.fail.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.constant.MqTags;
import com.hp.sh.expv3.bb.module.fail.dao.BBMqMsgDAO;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BbOrderCancelMqMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBMqMsgService{
	
    /** 起始时间戳 */
    private final long twepoch = 1541606400000L - 1000 * 3600 * 24;

	@Autowired
	private BBMqMsgDAO bBMqMsgDAO;

	public void delete(Long userId, Long id){
		this.bBMqMsgDAO.delete(userId, id);
	}

	@LockIt(key="mm-${msg.accountId}-${msg.orderId}")
	public void save(String tag, BBTradeVo msg, String exMessage) {
		
		if(exMessage!=null && exMessage.length()>1500){
			exMessage = exMessage.substring(0, 1500);
		}
		
		BBMqMsg msgEntity = new BBMqMsg();
		msgEntity.setUserId(msg.getAccountId());
		
		msgEntity.setAsset(msg.getAsset());
		msgEntity.setSymbol(msg.getSymbol());
		msgEntity.setExMessage(exMessage);
		
		msgEntity.setMessageId(msg.getMsgId());
		msgEntity.setTag(tag);
		msgEntity.setKey(""+msg.getOrderId());
		msgEntity.setBody(JsonUtils.toJson(msg));
		
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setSortId(this.getSortId(tag, msgEntity.getCreated()));
		
		this.bBMqMsgDAO.save(msgEntity);
	}

	private Long getSortId(String tag, Long created) {
		long tagbit = 1L<<60;
		if(tag.equals(MqTags.TAGS_TRADE)){
			tagbit = 0;
		}
		long sortId = tagbit | created;
		return sortId;
	}

	@LockIt(key="mm-${msg.accountId}-${msg.orderId}")
	public void saveIfNotExists(String tag, BbOrderCancelMqMsg msg, String exMessage) {
		
		if(exMessage!=null && exMessage.length()>1500){
			exMessage = exMessage.substring(0, 1500);
		}
		
		boolean existCancelled = this.exist(msg.getAccountId(), tag, ""+msg.getOrderId());
		if(existCancelled){
			return;
		}
		
		BBMqMsg msgEntity = new BBMqMsg();
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setUserId(msg.getAccountId());
		msgEntity.setAsset(msg.getAsset());
		msgEntity.setSymbol(msg.getSymbol());
		msgEntity.setExMessage(exMessage);
		msgEntity.setTag(tag);
		msgEntity.setMessageId(msg.getMsgId());
		msgEntity.setKey(""+msg.getOrderId());
		msgEntity.setBody(JsonUtils.toJson(msg));
		msgEntity.setSortId(this.getSortId(tag, msgEntity.getCreated()));
		this.bBMqMsgDAO.save(msgEntity);
	}
	
	@LockIt(key="mm-${userId}-${key}")
	public boolean exist(Long userId, String tag, String key){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
		params.put("orderBy", "id");
		params.put("asc", true);

		params.put("userId", userId);
		params.put("tag", tag);
		params.put("key", key);
		
		BBMqMsg msg = this.bBMqMsgDAO.queryOne(params);
		return msg!=null;
	}

	public BBMqMsg findFirst(String tag, String symbol){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
		params.put("orderBy", "sort_id");
		params.put("asc", true);
		params.put("tag", tag);
		params.put("symbol", symbol);
		BBMqMsg msg = this.bBMqMsgDAO.queryOne(params);
		return msg;
	}

	public List<BBMqMsg> findFirstList(int pageSize){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, pageSize, 1000L));
		params.put("orderBy", "sort_id");
		params.put("asc", true);
		List<BBMqMsg> msgList = this.bBMqMsgDAO.queryList(params);
		return msgList;
	}
	
}
