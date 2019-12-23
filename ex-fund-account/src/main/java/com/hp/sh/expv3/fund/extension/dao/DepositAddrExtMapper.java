package com.hp.sh.expv3.fund.extension.dao;

import com.hp.sh.expv3.fund.extension.vo.AddressVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/14
 */
public interface DepositAddrExtMapper {


    String getAddressByUserIdAndAsset(@Param("userId") Long userId,@Param("asset") String asset);

    List<AddressVo> getAddresses(@Param("userId") Long userId,@Param("asset") String asset);
}
