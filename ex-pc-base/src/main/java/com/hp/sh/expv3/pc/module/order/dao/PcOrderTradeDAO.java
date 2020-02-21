
package com.hp.sh.expv3.pc.module.order.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.base.mapper.BaseUserDataMapper;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.strategy.vo.OrderTradeVo;

/**
 * 
 * @author wangjg
 *
 */
public interface PcOrderTradeDAO extends BaseUserDataMapper<PcOrderTrade,Long> {

	public List<PcOrderTrade> queryList(Map<String,Object> params);
	
	public PcOrderTrade queryOne(Map<String,Object> params);

	public Long queryCount(Map<String,Object> params);

	public Long exist(@Param("userId") Long userId, @Param("tradeSn") String tradeSn);

	public List<OrderTradeVo> queryOrderTrade(@Param("userId") Long userId, @Param("orderIdList") List<Long> orderIdList);

	public void setSynchStatus(@Param("userId") Long userId, @Param("id") Long id, @Param("feeSynchStatus") Integer feeSynchStatus, @Param("modified") Long modified);


}
