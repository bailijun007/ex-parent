package com.hp.sh.expv3.fund.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.dao.WithdrawalAddrExtMapper;
import com.hp.sh.expv3.fund.extension.service.WithdrawalAddrExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WithdrawalAddrExtServiceImpl implements WithdrawalAddrExtService {
    @Autowired
    private WithdrawalAddrExtMapper withdrawalAddrExtMapper;

    @Override
    public WithdrawalAddrVo getAddressByUserIdAndAsset(Long userId, String asset) {
        return    withdrawalAddrExtMapper.getAddressByUserIdAndAsset(userId, asset);
    }

    @Override
    public PageResult<WithdrawalAddrVo> pageQueryWithdrawalAddrList(Long userId, String asset, Integer pageNo, Integer pageSize, Integer enabled) {
        PageResult<WithdrawalAddrVo> result=new PageResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("enabled", enabled);
        List<WithdrawalAddrVo> voList = withdrawalAddrExtMapper.findWithdrawalAddr(map);
       if(!CollectionUtils.isEmpty(voList)){
           List<WithdrawalAddrVo> list = voList.stream().skip(pageSize * (pageNo - 1))
                   .limit(pageSize)
                   .collect(Collectors.toList());
           result.setList(list);
       }
        result.setPageNo(pageNo);
        int rowTotal = voList.size();
        result.setRowTotal(Long.parseLong(rowTotal+""));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return result;
    }
}
