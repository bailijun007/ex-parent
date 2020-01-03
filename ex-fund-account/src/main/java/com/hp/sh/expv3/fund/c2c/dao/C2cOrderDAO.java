
package com.hp.sh.expv3.fund.c2c.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;

/**
 * 
 * @author wangjg
 *
 */
public interface C2cOrderDAO  {

	public List<C2cOrder> queryList(Map<String,Object> params);
	
	public C2cOrder queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

   int saveC2cOrder(C2cOrder c2cOrder);

}
