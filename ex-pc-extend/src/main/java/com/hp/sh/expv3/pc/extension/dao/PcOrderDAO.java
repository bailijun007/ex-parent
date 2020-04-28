package com.hp.sh.expv3.pc.extension.dao;

import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcOrderDAO {

    BigDecimal getGrossMargin(@Param("userId") Long userId,@Param("asset") String asset);

    PcOrderVo queryOne(Map<String,Object> map);

    List<PcOrderVo> queryList(Map<String,Object> map);


    List<PcOrderVo> queryOrders(Map<String, Object> map);

    List<PcOrderVo> queryOrderList(Map<String, Object> map);

    //查询总条数
    Long queryCount(Map<String, Object> map);

    List<PcOrderVo> queryUserActivityOrder(Map<String, Object> map);

	List<PcOrderVo> queryNextList(Map<String, Object> map);

    List<PcOrderVo> queryActivityOrder(Map<String, Object> map);

    BigDecimal queryTotalFee(@Param("startTime") Long startTime,@Param("endTime") Long endTime);

    BigDecimal queryTotalOrder(@Param("startTime") Long startTime,@Param("endTime") Long endTime);

    List<PcOrderVo> queryPcActivityOrder(Map<String, Object> map);
}
