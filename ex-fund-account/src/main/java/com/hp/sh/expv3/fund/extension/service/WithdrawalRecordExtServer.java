package com.hp.sh.expv3.fund.extension.service;

import com.hp.sh.expv3.fund.extension.dao.WithdrawalRecordExtMapper;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * 体现记录扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WithdrawalRecordExtServer {
    @Autowired
    private WithdrawalRecordExtMapper withdrawalRecordExtMapper;

    public BigDecimal getFrozenCapital(Long userId, String asset) {
        return withdrawalRecordExtMapper.getFrozenCapital(userId,asset);
    }
}
