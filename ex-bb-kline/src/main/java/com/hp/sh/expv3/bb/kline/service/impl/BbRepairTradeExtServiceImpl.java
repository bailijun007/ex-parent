package com.hp.sh.expv3.bb.kline.service.impl;

import com.hp.sh.expv3.bb.kline.dao.BbRepairTradeMapper;
import com.hp.sh.expv3.bb.kline.service.BbRepairTradeExtService;
import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbRepairTradeExtServiceImpl implements BbRepairTradeExtService {

    @Autowired
    private BbRepairTradeMapper bbRepairTradeMapper;

    /**
     *  拆成不同的分钟
     * @param asset
     * @param symbol
     * @param ms
     * @param maxMs
     * @return
     */
    @Override
    public List<BbRepairTradeVo> listRepairTrades(String asset, String symbol, long ms, long maxMs) {
         List<BbRepairTradeVo> voList = bbRepairTradeMapper.listRepairTrades(asset, symbol, ms, maxMs);
         return voList;
    }
}
