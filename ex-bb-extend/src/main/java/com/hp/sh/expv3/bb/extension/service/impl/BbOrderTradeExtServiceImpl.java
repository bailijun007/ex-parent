package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.dao.BbOrderTradeExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbOrderTradeExtServiceImpl implements BbOrderTradeExtService {
    @Autowired
    private BbOrderTradeExtMapper bbOrderTradeExtMapper;

    @Override
    public BbOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime) {
        return bbOrderTradeExtMapper.selectLessTimeTrade(asset,symbol,statTime);
    }
}
