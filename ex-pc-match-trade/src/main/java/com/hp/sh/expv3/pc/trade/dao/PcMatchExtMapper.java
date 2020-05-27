package com.hp.sh.expv3.pc.trade.dao;

import com.hp.sh.expv3.pc.trade.pojo.PcMatchExtVo;
import com.hp.sh.expv3.vo.PcTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public interface PcMatchExtMapper {

    void batchSave(@Param("trades") List<PcMatchExtVo> trades, @Param("table") String table);


    void save(@Param("tradeVo") PcMatchExtVo tradeVo);

    List<PcTradeVo> queryList(Map<String, Object> map);

    List<PcTradeVo> queryTradeByGtTime(Map<String, Object> map);

    PcTradeVo queryLastTrade(String asset, String symbol, Long startTime);

    List<PcTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime, Long userId);
}
