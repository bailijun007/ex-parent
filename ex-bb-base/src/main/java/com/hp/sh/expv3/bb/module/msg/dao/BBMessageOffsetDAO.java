
package com.hp.sh.expv3.bb.module.msg.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseMapper;
import com.hp.sh.expv3.bb.module.msg.entity.BBMessageOffset;

/**
 * 
 * @author wangjg
 *
 */
public interface BBMessageOffsetDAO extends BaseMapper<BBMessageOffset,Long> {
	
	public BBMessageOffset findById(@Param("shardId") Long shardId);
	
	public List<BBMessageOffset> queryList(Map<String,Object> params);
	
	public BBMessageOffset queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
