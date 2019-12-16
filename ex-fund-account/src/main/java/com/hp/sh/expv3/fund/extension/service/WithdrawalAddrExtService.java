package com.hp.sh.expv3.fund.extension.service;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface WithdrawalAddrExtService {
    String getAddressByUserIdAndAsset(Long userId, String asset);
}
