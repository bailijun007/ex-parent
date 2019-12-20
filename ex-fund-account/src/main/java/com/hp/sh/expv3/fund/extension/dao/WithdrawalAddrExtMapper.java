package com.hp.sh.expv3.fund.extension.dao;

import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface WithdrawalAddrExtMapper {


    List<WithdrawalAddrVo> getAddressByUserIdAndAsset(@Param("userId") Long userId, @Param("asset")String asset);

    List<WithdrawalAddrVo> findWithdrawalAddr( Map<String, Object> map);
}
