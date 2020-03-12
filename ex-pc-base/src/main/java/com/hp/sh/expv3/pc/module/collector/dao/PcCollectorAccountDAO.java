
package com.hp.sh.expv3.pc.module.collector.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.pc.module.collector.entity.PcCollectorAccount;

/**
 * 
 * @author wangjg
 *
 */
public interface PcCollectorAccountDAO {
	
	public PcCollectorAccount get(@Param("id") Long id);
	
	void save(PcCollectorAccount entity);

	int update(PcCollectorAccount entity);

}
