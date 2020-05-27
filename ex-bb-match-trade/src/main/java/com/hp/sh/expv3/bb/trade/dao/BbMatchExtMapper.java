package com.hp.sh.expv3.bb.trade.dao;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@Mapper
public interface BbMatchExtMapper {

    void batchSave(@Param("trades") List<BbMatchExtVo> trades, @Param("table")String table);


    void save(@Param("tradeVo") BbMatchExtVo tradeVo);

    List<BbTradeVo> queryList(Map<String, Object> map);

    List<BbTradeVo> selectTradeListByTimeInterval(@Param("asset") String asset, @Param("symbol") String symbol, @Param("tradeTimeBegin")  Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd);

    List<BbTradeVo> selectTradeListByUser(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol,  @Param("tradeTimeBegin")  Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd);

    List<BbTradeVo> queryTradeList(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol") String symbol, @Param("limit") Integer limit,@Param("tradeTimeBegin") Long tradeTimeBegin,@Param("tradeTimeEnd")Long tradeTimeEnd);

    BbTradeVo queryLastTradeByLtTime(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTimeBegin")  Long tradeTimeBegin,@Param("tradeTimeEnd")  Long tradeTimeEnd);

}
