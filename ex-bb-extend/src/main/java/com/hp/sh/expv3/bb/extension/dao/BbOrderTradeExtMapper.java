package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbOrderTradeExtMapper {
    BbOrderTradeVo queryOne(Map<String,Object> map);

    List<BbOrderTradeVo> queryList(Map<String,Object> map);

    Long queryCount();

    BbOrderTradeVo selectLessTimeTrade(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTime") Long tradeTime);

    List<BbOrderTradeVo> selectAllTradeListByUser(@Param("asset") String asset,@Param("symbol") String symbol,@Param("userId") Long userId);

    List<BbOrderTradeVo> queryOrderTrade(@Param("userId") Long userId, @Param("orderIdList") List<Long> orderIdList);
}
