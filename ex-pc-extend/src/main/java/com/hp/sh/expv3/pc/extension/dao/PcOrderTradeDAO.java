package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeExtendVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/20
 */
public interface PcOrderTradeDAO {



    PcOrderTradeVo queryOne(Map<String,Object> map);

    List<PcOrderTradeVo> queryList(Map<String,Object> map);

    BigDecimal getRealisedPnl(@Param("posId") Long posId, @Param("userId") Long userId, @Param("orderId") Long orderId);

    List<PcOrderTradeVo> queryTradeRecords(Map<String, Object> map);

    PcOrderTradeVo selectLessTimeTrade(Map<String, Object> map);

    List<PcOrderTradeVo> selectAllTradeListByUser(Map<String, Object> map);

    List<PcOrderTradeVo> selectTradeListByTimeInterval(Map<String, Object> map);

    List<PcOrderTradeVo> queryLastTradeRecord(Map<String, Object> map);

    List<PcOrderTradeVo> selectPcFeeCollectByAccountId(Map<String, Object> map);

    List<PcOrderTradeExtendVo> selectTradeListByUserId(@Param("asset") String asset, @Param("symbol") String symbol, @Param("tradeTimeBegin")  Long tradeTimeBegin, @Param("tradeTimeEnd") Long tradeTimeEnd, @Param("userId") Long userId);

    BigDecimal queryPcTradeFee(@Param("userId") Long userId, @Param("asset")String asset,@Param("makerFlag") Integer makerFlag,@Param("beginTime") Long beginTime, @Param("endTime") Long endTime);

    List<PcOrderTradeDetailVo> queryHistory(Map<String, Object> map);
}
