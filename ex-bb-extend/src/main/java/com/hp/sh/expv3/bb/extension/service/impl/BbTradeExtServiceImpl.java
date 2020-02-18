package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.dao.BbTradeExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbTradeExtServiceImpl implements BbTradeExtService {
    @Autowired
    private BbTradeExtMapper bbTradeExtMapper;

    @Override
    public List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime) {
        return bbTradeExtMapper.selectTradeListByTimeInterval(asset,symbol,startTime,endTime);
    }
}
