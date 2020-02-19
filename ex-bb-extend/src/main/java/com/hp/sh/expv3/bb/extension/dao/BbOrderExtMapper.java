package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.bb.strategy.vo.OrderTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbOrderExtMapper {

    BbOrderVo queryOne(Map<String,Object> map);

    List<BbOrderVo> queryList(Map<String,Object> map);

    List<OrderTradeVo> queryOrderTrade(@Param("userId") Long userId, @Param("orderIdList") List<Long> orderIdList);
}
