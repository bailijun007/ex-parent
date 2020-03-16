package com.hp.sh.expv3.bb.kline.dao;

import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public interface BbRepairTradeMapper {

    void save(BbRepairTradeVo tradeVo);

    void batchSave(@Param("trades") List<BbRepairTradeVo> trades);

    int batchUpdate(@Param("trades") List<BbRepairTradeVo> trades,@Param("tradeTimeBegin") Long tradeTimeBegin, @Param("tradeTimeEnd") long tradeTimeEnd);

    List<BbRepairTradeVo> listRepairTrades(@Param("asset") String asset,@Param("symbol") String symbol, @Param("tradeTimeBegin")long tradeTimeBegin, @Param("tradeTimeEnd")long tradeTimeEnd);
}
