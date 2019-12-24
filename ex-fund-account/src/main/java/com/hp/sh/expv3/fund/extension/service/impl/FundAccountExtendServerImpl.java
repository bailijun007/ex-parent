package com.hp.sh.expv3.fund.extension.service.impl;

import com.hp.sh.expv3.fund.extension.dao.FundAccountExtendMapper;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendService;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FundAccountExtendServerImpl implements FundAccountExtendService {
    @Autowired
    private FundAccountExtendMapper fundAccountExtendMapper;


    @Override
    public CapitalAccountVo getCapitalAccount(Long userId, String asset) {
        return fundAccountExtendMapper.getCapitalAccount(userId,asset);
    }

    @Override
    public List<CapitalAccountVo> fundAccountList(Long userId, String asset) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        List<CapitalAccountVo> voList = fundAccountExtendMapper.queryList(map);
        return voList;
    }
}
