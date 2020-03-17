package com.hp.sh.expv3.fund.extension.dao;

import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface FundTransferExtMapper {

    List<FundTransferExtVo> queryHistory(@Param("userId") Long userId,@Param("asset") String asset,
                                         @Param("queryId") Long queryId,  @Param("pageSize") Integer pageSize,
                                         @Param("pageStatus") Integer pageStatus);

    List<FundTransferExtVo> queryList(Map<String,Object> map);

    Long queryCount(@Param("userId") Long userId,@Param("asset") String asset,
                   @Param("queryId") Long queryId, @Param("pageStatus") Integer pageStatus);
}
