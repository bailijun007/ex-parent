package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/20
 */
public interface PcOrderTradeDAO {


    BigDecimal getPl(@Param("userId") Long userId, @Param("asset") String asset,@Param("posId") Long posId);

    PcOrderTradeVo queryOne(Map<String,Object> map);

    List<PcOrderTradeVo> queryList(Map<String,Object> map);

    BigDecimal getRealisedPnl(@Param("posId") Long posId, @Param("userId") Long userId, @Param("orderId") Long orderId);

    List<PcOrderTradeVo> queryTradeRecords(Map<String, Object> map);

    PcOrderTradeVo selectLessTimeTrade(Map<String, Object> map);

    List<PcOrderTradeVo> selectAllTradeListByUser(Map<String, Object> map);

    List<PcOrderTradeVo> selectTradeListByTimeInterval(Map<String, Object> map);

    List<PcOrderTradeVo> queryLastTradeRecord(Map<String, Object> map);
}
