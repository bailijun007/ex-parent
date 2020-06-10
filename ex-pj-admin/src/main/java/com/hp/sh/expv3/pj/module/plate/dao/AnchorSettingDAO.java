
package com.hp.sh.expv3.pj.module.plate.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.pj.base.BaseMapper;
import com.hp.sh.expv3.pj.module.plate.entity.AnchorSetting;

/**
 * 
 * @author wangjg
 *
 */
public interface AnchorSettingDAO extends BaseMapper<AnchorSetting,Long> {

	public List<AnchorSetting> queryList(Map<String,Object> params);
	
	public AnchorSetting queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public AnchorSetting findBySymbol(@Param("asset") String asset, @Param("symbol") String symbol);

}
