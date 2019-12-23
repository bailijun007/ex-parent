package com.hp.sh.expv3.fund.extension.service.impl;

import com.hp.sh.expv3.fund.extension.dao.DepositAddrExtMapper;
import com.hp.sh.expv3.fund.extension.service.DepositAddrExtService;
import com.hp.sh.expv3.fund.extension.vo.AddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 充币地址扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositAddrExtServiceImpl implements DepositAddrExtService {
    @Autowired
    private DepositAddrExtMapper depositAddrExtMapper;

    @Override
    public String getAddressByUserIdAndAsset(Long userId, String asset) {
        return depositAddrExtMapper.getAddressByUserIdAndAsset(userId,asset);
    }

    @Override
    public AddressVo getAddresses(Long userId, String asset) {
        return depositAddrExtMapper.getAddresses(userId,asset);
    }
}
