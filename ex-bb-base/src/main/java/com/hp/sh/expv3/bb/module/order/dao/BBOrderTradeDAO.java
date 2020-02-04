
package com.hp.sh.expv3.bb.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.strategy.vo.OrderTradeVo;

/**
 * 
 * @author wangjg
 *
 */
public interface BBOrderTradeDAO extends BaseUserDataMapper<BBOrderTrade,Long> {

	public List<BBOrderTrade> queryList(Map<String,Object> params);
	
	public BBOrderTrade queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public Long exist(@Param("userId") Long userId, @Param("tradeSn") String tradeSn);

	public List<OrderTradeVo> queryOrderTrade(@Param("userId") Long userId, @Param("orderIdList") List<Long> orderIdList);

}
