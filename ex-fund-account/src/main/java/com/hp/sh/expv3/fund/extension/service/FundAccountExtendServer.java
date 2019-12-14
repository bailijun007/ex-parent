package com.hp.sh.expv3.fund.extension.service;

import com.hp.sh.expv3.fund.extension.dao.FundAccountExtendMapper;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiLiJun  on 2019/12/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FundAccountExtendServer {
    @Autowired
    private FundAccountExtendMapper fundAccountExtendMapper;


    public CapitalAccountVo getCapitalAccount(Long userId, String asset) {
        return fundAccountExtendMapper.getCapitalAccount(userId,asset);
    }
}
