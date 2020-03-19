
package com.hp.sh.expv3.pc.module.liq.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.gitee.hupadev.commons.page.Page;
import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.pc.module.liq.entity.PcLiqRecord;

/**
 * 
 * @author wangjg
 *
 */
public interface PcLiqRecordDAO extends BaseUserDataMapper<PcLiqRecord,Long> {

	public List<PcLiqRecord> queryList(Map<String,Object> params);
	
	public PcLiqRecord queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);
	
	public List<PcLiqRecord> queryPending(Page page, @Param("userId") Long userId, @Param("startTime") Long startTime, @Param("startId") Long startId);

}
