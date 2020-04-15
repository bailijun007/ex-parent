
package com.hp.sh.expv3.bb.module.fail.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gitee.hupadev.commons.json.JsonUtils;
import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.bb.module.fail.dao.BBMqMsgDAO;
import com.hp.sh.expv3.bb.module.fail.entity.BBMqMsg;
import com.hp.sh.expv3.bb.mq.msg.in.BbOrderCancelMqMsg;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class BBMqMsgService{

	@Autowired
	private BBMqMsgDAO bBMqMsgDAO;

	public BBMqMsg findById(Long userId, Long id) {
		return this.bBMqMsgDAO.findById(userId, id);
	}

	public void save(BBMqMsg msgEntity){
		Long now = DbDateUtils.now();
		msgEntity.setCreated(now);
		this.bBMqMsgDAO.save(msgEntity);
	}

	public void delete(Long userId, Long id){
		this.bBMqMsgDAO.delete(userId, id);
	}

	public void save(String tag, BBTradeVo msg) {
		BBMqMsg msgEntity = new BBMqMsg();
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setUserId(msg.getAccountId());
		msgEntity.setTag(tag);
		msgEntity.setKey(""+msg.getOrderId());
		msgEntity.setBody(JsonUtils.toJson(msg));
		this.bBMqMsgDAO.save(msgEntity);
	}

	public void save(String tag, BbOrderCancelMqMsg msg) {
		BBMqMsg msgEntity = new BBMqMsg();
		msgEntity.setCreated(DbDateUtils.now());
		msgEntity.setUserId(msg.getAccountId());
		msgEntity.setTag(tag);
		msgEntity.setKey(""+msg.getOrderId());
		msgEntity.setBody(JsonUtils.toJson(msg));
		this.bBMqMsgDAO.save(msgEntity);
	}

	public BBMqMsg findFirst(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 1, 1000L));
		params.put("orderBy", "id");
		params.put("asc", true);
		BBMqMsg msg = this.bBMqMsgDAO.queryOne(params);
		return msg;
	}

	public List<BBMqMsg> findFirstList(){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("page", new Page(1, 100, 1000L));
		params.put("orderBy", "id");
		params.put("asc", true);
		List<BBMqMsg> msgList = this.bBMqMsgDAO.queryList(params);
		return msgList;
	}
	
}
