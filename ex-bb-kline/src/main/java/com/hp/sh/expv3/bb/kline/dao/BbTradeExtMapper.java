package com.hp.sh.expv3.bb.kline.dao;

import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public interface BbTradeExtMapper {

    List<BbTradeVo> queryByTimeInterval(@Param("asset") String asset, @Param("symbol") String symbol, @Param("tradeTimeBegin")  Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd);

}
