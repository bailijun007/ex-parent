package com.hp.sh.expv3.bb.extension.dao;

import org.apache.ibatis.annotations.Param;

import com.hp.sh.expv3.bb.extension.vo.PcTradeVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcTradeDAO {

    PcTradeVo queryOne(Map<String,Object> map);

    List<PcTradeVo> queryList(Map<String,Object> map);

    List<PcTradeVo> queryTradeByGtTime(Map<String, Object> map);

    List<PcTradeVo> selectTradeListByTimeInterval(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTimeBegin")  Long tradeTimeBegin,@Param("tradeTimeEnd") Long tradeTimeEnd,@Param("userId") Long userId);

    PcTradeVo queryLastTrade(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTime")  Long tradeTime);

    List<PcTradeVo> selectTradeListByUserId(@Param("asset") String asset,@Param("symbol") String symbol,@Param("tradeTimeBegin")  Long tradeTimeBegin,@Param("tradeTimeEnd") Long tradeTimeEnd,@Param("userId") Long userId);
}
