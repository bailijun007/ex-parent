
package com.hp.sh.expv3.pj.module.plate.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.pj.base.BaseMapper;
import com.hp.sh.expv3.pj.module.plate.entity.PlateStrategy;

/**
 * 
 * @author wangjg
 *
 */
public interface PlateStrategyDAO extends BaseMapper<PlateStrategy,Long> {

	public List<PlateStrategy> queryList(Map<String,Object> params);
	
	public PlateStrategy queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public PlateStrategy findBySymbol(@Param("asset") String asset, @Param("symbol") String symbol);

}
