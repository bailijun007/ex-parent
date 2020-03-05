package com.hp.sh.expv3.fund.extension.dao;

import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/14
 */
public interface WithdrawalRecordExtMapper {


    BigDecimal getFrozenCapital(@Param("userId") Long userId,@Param("asset") String asset,@Param("approvalStatus") int approvalStatus,@Param("payStatus") int payStatus);

    List<WithdrawalRecordVo> queryHistory(@Param("userId") Long userId,@Param("asset") String asset, @Param("queryId") Long queryId,
                                          @Param("pageSize") Integer pageSize,  @Param("pageStatus")  Integer pageStatus);

    List<WithdrawalRecordVo> queryHistoryByTime(Map<String,Object> map);

    WithdrawalRecordVo queryLastHistory(@Param("userId") Long userId,@Param("asset") String asset);

    List<WithdrawalRecordVo> queryByUserIdAndAsset(@Param("userId") Long userId,@Param("asset") String asset);

    List<WithdrawalRecordVo> queryList(Map<String, Object> map);
}
