package com.hp.sh.expv3.fund.extension.service;

import com.hp.sh.expv3.fund.extension.dao.DepositAddrExtMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 充币地址扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositAddrExtService {
    @Autowired
    private DepositAddrExtMapper depositAddrExtMapper;

    public String getAddressByUserIdAndAsset(Long userId, String asset) {
        return depositAddrExtMapper.getAddressByUserIdAndAsset(userId,asset);
    }
}
