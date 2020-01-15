package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionStatVo;

import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcPositionDAO {
    BigDecimal getPosMargin(@Param("userId") Long userId, @Param("asset") String asset);

    BigDecimal getInitMargin(@Param("userId") Long userId, @Param("asset") String asset,@Param("posId")  Long posId);

    PcPositionVo queryOne(Map<String,Object> map);

    List<PcPositionVo> queryList(Map<String,Object> map);

    List<PcPositionVo> queryActivePosition(Map<String,Object> map);

    BigDecimal getAvgPrice(@Param("userId") Long userId, @Param("asset") String asset, @Param("symbol")  String symbol, @Param("volume") Integer volume);

    List<PcPositionVo> selectPosByAccount(Map<String, Object> map);

    List<PcSymbolPositionStatVo> getSymbolPositionStat(@Param("asset") String asset, @Param("symbol")  String symbol);
}
