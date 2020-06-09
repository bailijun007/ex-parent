
package com.hp.sh.expv3.pj.module.sys.mapper;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.pj.base.BaseMapper;
import com.hp.sh.expv3.pj.module.sys.entity.SysSetting;

/**
 * 
 * @author wangjg
 *
 */
public interface SysSettingDAO extends BaseMapper<SysSetting,Long> {

	public List<SysSetting> queryList(Map<String,Object> params);
	
	public SysSetting queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
