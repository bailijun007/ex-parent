package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.dao.BbTradeExtMapper;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.BbTradeExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/11
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbTradeExtServiceImpl implements BbTradeExtService {

    @Autowired
    private BbTradeExtMapper bbTradeExtMapper;

    @Override
    public List<BbTradeVo> queryByTimeInterval(String asset, String symbol, long startTimeInMs, long endTimeInMs) {
        return bbTradeExtMapper.queryByTimeInterval(asset,symbol,startTimeInMs,endTimeInMs);
    }
}
