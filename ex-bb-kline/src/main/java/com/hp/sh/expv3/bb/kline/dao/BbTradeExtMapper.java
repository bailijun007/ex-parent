package com.hp.sh.expv3.bb.kline.dao;

import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public interface BbTradeExtMapper {

    List<BbTradeVo> queryByTimeInterval(@Param("id") Long id,@Param("asset") String asset, @Param("symbol") String symbol, @Param("tradeTimeBegin") Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd,@Param("endLimit") int endLimit,@Param("table") String table);

    int update(@Param("enableFlag") int enableFlag,@Param("asset") String asset, @Param("symbol") String symbol, @Param("tradeTimeBegin") Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd,@Param("table") String table);


}
