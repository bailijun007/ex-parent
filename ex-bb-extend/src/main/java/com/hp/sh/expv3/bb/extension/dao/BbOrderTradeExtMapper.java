package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeDetailVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbUserOrderTrade;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbOrderTradeExtMapper {
    BbOrderTradeVo queryOne(Map<String,Object> map);

    List<BbOrderTradeVo> queryList(Map<String,Object> map);

    Long queryCount();

    BbOrderTradeVo selectLessTimeTrade(@Param("asset") String asset,@Param("symbol") String symbol,@Param("startTime") Long startTime);

    List<BbOrderTradeVo> selectAllTradeListByUser(@Param("asset") String asset,@Param("symbol") String symbol,@Param("userId") Long userId);

    List<BbOrderTradeVo> queryOrderTrade(@Param("userId") Long userId, @Param("orderIdList") List<Long> orderIdList);

    List<BbUserOrderTrade> selectTradeListByUserId(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTimeBegin")  Long tradeTimeBegin,@Param("tradeTimeEnd") Long tradeTimeEnd,@Param("userId") Long userId, @Param("id") Long id);

    List<BbOrderTradeDetailVo> selectPcFeeCollectByAccountId(Map<String, Object> map);

    List<BbOrderTradeVo> queryByIds(@Param("idList") List<Long> idList);

    List<BbOrderTradeDetailVo> queryHistory(Map<String, Object> map);

    BigDecimal queryTradeNumberTotalByTime(@Param("asset")  String asset,@Param("symbol")  String symbol,@Param("tradeTimeBegin") Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd);
}
