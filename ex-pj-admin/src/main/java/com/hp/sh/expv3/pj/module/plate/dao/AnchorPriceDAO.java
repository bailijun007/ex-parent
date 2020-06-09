
package com.hp.sh.expv3.pj.module.plate.dao;

import java.util.List;
import java.util.Map;

import com.hp.sh.expv3.pj.base.BaseMapper;
import com.hp.sh.expv3.pj.module.plate.entity.AnchorPrice;

/**
 * 
 * @author wangjg
 *
 */
public interface AnchorPriceDAO extends BaseMapper<AnchorPrice, Long> {
	
	public List<AnchorPrice> queryList(Map<String, Object> params);

	public AnchorPrice queryOne(Map<String, Object> params);

	public Long queryCount(Map<String, Object> params);
}
