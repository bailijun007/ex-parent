package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.dao.BbTradeExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public List<BbTradeVo> selectTradeListByUser(Long userId, String asset, String symbol, Long startTime, Long endTime) {
        return bbTradeExtMapper.selectTradeListByUser(userId,asset,symbol,startTime,endTime);
    }

    @Override
    public List<BbTradeVo> queryTradeList(Long userId, String asset, String symbol, Integer count) {
        return bbTradeExtMapper.queryTradeList(userId,asset,symbol,count);
    }

    @Override
    public BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long startTime) {
        return bbTradeExtMapper.queryLastTradeByLtTime(asset,symbol,startTime);
    }

    @Override
    public List<BbTradeVo> queryLastTrade(String asset, String symbol, Integer count) {
        Map<String, Object> map=new HashMap<>();
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderBy","trade_time");
        map.put("limit",count);
        return bbTradeExtMapper.queryLastTrade(map);
    }

//    @Override
//    public List<BbTradeVo> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId) {
//        List<BbTradeVo> list = bbTradeExtMapper.selectTradeListByUserId(asset,symbol,startTime,endTime,userId);
//        return list;
//
//    }

    @Override
    public List<BbTradeVo> queryByTimeInterval(String asset, String symbol, long startTimeInMs, long endTimeInMs) {
        return bbTradeExtMapper.queryByTimeInterval(asset,symbol,startTimeInMs,endTimeInMs);
    }
}
