package com.hp.sh.expv3.fund.extension.dao;

import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/14
 */
public interface DepositRecordExtMapper {


    List<DepositRecordHistoryVo> queryHistory(@Param("userId") Long userId,@Param("asset") String asset,@Param("queryId") Long queryId,
                                              @Param("pageSize") Integer pageSize,@Param("pageStatus") Integer pageStatus);

    List<DepositRecordHistoryVo> queryByUserIdAndAsset(@Param("userId") Long userId,@Param("asset") String asset);

    BigDecimal queryTotalNumber(@Param("asset") String asset,@Param("payStatus") Integer payStatus);
}
