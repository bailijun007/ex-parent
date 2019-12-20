package com.hp.sh.expv3.fund.extension.service.impl;

import com.hp.sh.expv3.fund.extension.dao.WithdrawalAddrExtMapper;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WithdrawalAddrExtServiceImpl implements WithdrawalAddrExtService {
    @Autowired
    private WithdrawalAddrExtMapper withdrawalAddrExtMapper;

    @Override
    public List<WithdrawalAddrVo> getAddressByUserIdAndAsset(Long userId, String asset) {
        List<WithdrawalAddrVo> voList = withdrawalAddrExtMapper.getAddressByUserIdAndAsset(userId, asset);
        return voList;

    }

    @Override
    public List<WithdrawalAddrVo> findWithdrawalAddr(Long userId, String asset) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        List<WithdrawalAddrVo> voList = withdrawalAddrExtMapper.findWithdrawalAddr(map);
        return voList;
    }
}
