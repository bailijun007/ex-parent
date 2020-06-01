package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.dao.BbOrderTradeExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.util.CommonDateUtils;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeDetailVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbUserOrderTrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return bbOrderTradeExtMapper.selectLessTimeTrade(asset, symbol, statTime);
    }

    @Override
    public List<BbOrderTradeVo> selectAllTradeListByUser(String asset, String symbol, Long userId) {
        return bbOrderTradeExtMapper.selectAllTradeListByUser(asset, symbol, userId);
    }

    @Override
    public List<BbOrderTradeVo> queryOrderTrade(Long userId, List<Long> orderIdList) {
        return bbOrderTradeExtMapper.queryOrderTrade(userId, orderIdList);
    }

    @Override
    public List<BbUserOrderTrade> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId, Long id) {
        List<BbUserOrderTrade> list = bbOrderTradeExtMapper.selectTradeListByUserId(asset, symbol, startTime, endTime, userId, id);
        return list;
    }

    @Override
    public List<BbOrderTradeDetailVo> selectPcFeeCollectByAccountId(String asset, String symbol, Long userId, Long statTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
        map.put("tradeTimeBegin", statTime);
        map.put("tradeTimeEnd", endTime);
        List<BbOrderTradeDetailVo> list = bbOrderTradeExtMapper.selectPcFeeCollectByAccountId(map);
        for (BbOrderTradeDetailVo bbOrderTradeDetailVo : list) {
            bbOrderTradeDetailVo.setAmt(bbOrderTradeDetailVo.getPrice().multiply(bbOrderTradeDetailVo.getQty()));
        }
        return list;
    }

    @Override
    public List<BbOrderTradeVo> queryByIds(List<Long> refIds) {

        return bbOrderTradeExtMapper.queryByIds(refIds);
    }

    @Override
    public List<BbOrderTradeDetailVo> queryHistory(Long userId, String asset, String symbol, Long lastTradeId, Integer nextPage, Integer pageSize, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("tradeTimeBegin", startTime);
        map.put("tradeTimeEnd", endTime);
        map.put("limit", pageSize);
        List<BbOrderTradeDetailVo> list = bbOrderTradeExtMapper.queryHistory(map);
        if (!CollectionUtils.isEmpty(list)) {
            for (BbOrderTradeDetailVo detailVo : list) {
                detailVo.setAmt(detailVo.getPrice().multiply(detailVo.getQty()));
            }
        }
        return list;
    }
}
