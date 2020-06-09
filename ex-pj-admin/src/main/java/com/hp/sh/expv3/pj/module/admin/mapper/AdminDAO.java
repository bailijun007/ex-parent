
package com.hp.sh.expv3.pj.module.admin.mapper;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.pj.base.BaseMapper;
import com.hp.sh.expv3.pj.module.admin.entity.Admin;

/**
 * 
 * @author wangjg
 *
 */
public interface AdminDAO extends BaseMapper<Admin,Long> {

	public List<Admin> queryList(Map<String,Object> params);
	
	public Admin queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

}
