package com.hp.sh.expv3.bb.extension.dao;

import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbOrderExtMapper {

    BbOrderVo queryOne(Map<String,Object> map);

    List<BbOrderVo> queryList(Map<String,Object> map);


    BigDecimal getLockAsset(@Param("userId") Long userId,@Param("asset") String asset);

    List<BbOrderVo> queryHistoryOrderList(Map<String, Object> map);

    List<BbOrderVo> queryHistoryByIsNextPage(Map<String, Object> map);

    List<BbOrderVo> queryBbActiveOrderList(Map<String, Object> map);

    List<BbOrderVo> queryBbActiveOrdersByIsNextPage(Map<String, Object> map);
}
