
package com.hp.sh.expv3.bb.module.msg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.bb.module.msg.dao.BBMessageOffsetDAO;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageOffset;
import com.hp.sh.expv3.utils.DbDateUtils;

/**
 * 
 * @author wangjg
 *
 */
@Service
@CacheConfig(cacheNames="shardOffset")
@Transactional(rollbackFor=Exception.class)
public class BBMessageOffsetService{

	@Autowired
	private BBMessageOffsetDAO messageOffsetDAO;
	
	@Autowired
	private BBMessageOffsetService self;
	
	@CachePut(key="#shardId")
	public Long cacheShardOffset(Integer shardId, Long offsetId){
		return offsetId;
	}
	
	@Cacheable(key="#shardId")
	public Long getCachedShardOffset(Integer shardId){
		BBMessageOffset offset = this.getMessageOffset(shardId);
		return offset.getReadedOffset();
	}
	
	public void persistCachedOffset(Integer shardId){
		Long offset = self.getCachedShardOffset(shardId);
		if(offset==null){
			return;
		}
		BBMessageOffset msgOffset = new BBMessageOffset();
		msgOffset.setModified(DbDateUtils.now());
		msgOffset.setShardId(shardId);
		msgOffset.setReadedOffset(offset);
		this.messageOffsetDAO.update(msgOffset);
	}

	public BBMessageOffset getMessageOffset(Integer shardId ) {
		BBMessageOffset offset = this.messageOffsetDAO.findById(shardId);
		if(offset==null){
			offset = new BBMessageOffset();
			offset.setShardId(shardId);
			offset.setModified(DbDateUtils.now());
			offset.setReadedOffset(null);
			this.messageOffsetDAO.save(offset);
		}
		return offset;
	}

}
