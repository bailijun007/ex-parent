package com.hp.sh.expv3.fund.extension.dao;

import org.apache.ibatis.annotations.Param;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface WithdrawalAddrExtMapper {


    String getAddressByUserIdAndAsset(@Param("userId") Long userId, @Param("asset")String asset);
}
