package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbTradeExtMapper {
    BbTradeVo queryOne(Map<String,Object> map);

    List<BbTradeVo> queryList(Map<String,Object> map);

    List<BbTradeVo> selectTradeListByTimeInterval(@Param("asset") String asset, @Param("symbol") String symbol, @Param("tradeTimeBegin")  Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd);

    List<BbTradeVo> selectTradeListByUser(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol,  @Param("tradeTimeBegin")  Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd);

    List<BbTradeVo> queryTradeList(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("limit") Integer limit,@Param("tradeTimeBegin") Long tradeTimeBegin,@Param("tradeTimeEnd")Long tradeTimeEnd);

    BbTradeVo queryLastTradeByLtTime(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTimeBegin")  Long tradeTimeBegin,@Param("tradeTimeEnd")  Long tradeTimeEnd);

    List<BbTradeVo> queryLastTrade(Map<String, Object> map);

//    List<BbTradeVo> selectTradeListByUserId(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTimeBegin")  Long tradeTimeBegin,@Param("tradeTimeEnd") Long tradeTimeEnd,@Param("userId") Long userId);

    List<BbTradeVo> queryByTimeInterval(@Param("asset") String asset, @Param("symbol") String symbol, @Param("tradeTimeBegin")  Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd);

}
