package com.hp.sh.expv3.fund.extension.service.impl;

import com.hp.sh.expv3.fund.extension.dao.WithdrawalAddrExtMapper;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WithdrawalAddrExtServiceImpl implements WithdrawalAddrExtService {
    @Autowired
    private WithdrawalAddrExtMapper withdrawalAddrExtMapper;

    @Override
    public String getAddressByUserIdAndAsset(Long userId, String asset) {

        return withdrawalAddrExtMapper.getAddressByUserIdAndAsset(userId,asset);
    }
}
