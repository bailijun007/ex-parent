package com.hp.sh.expv3.bb.kline.dao;

import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public interface BbRepairTradeMapper {

    void save(@Param("tradeVo") BbRepairTradeVo tradeVo,@Param("table") String table);

    void batchSave(@Param("trades") List<BbRepairTradeVo> trades,@Param("table") String table);

    void batchUpdate(@Param("trades") List<BbRepairTradeVo> trades,@Param("tradeTimeBegin") Long tradeTimeBegin, @Param("tradeTimeEnd") long tradeTimeEnd,@Param("table") String table);

    List<BbRepairTradeVo> listRepairTrades(@Param("asset") String asset,@Param("symbol") String symbol, @Param("tradeTimeBegin")long tradeTimeBegin, @Param("tradeTimeEnd")long tradeTimeEnd,@Param("table") String table);

    void batchCancel(@Param("asset") String asset,@Param("symbol") String symbol, @Param("tradeTimeBegin")long tradeTimeBegin, @Param("tradeTimeEnd")long tradeTimeEnd,@Param("table") String table);
}
