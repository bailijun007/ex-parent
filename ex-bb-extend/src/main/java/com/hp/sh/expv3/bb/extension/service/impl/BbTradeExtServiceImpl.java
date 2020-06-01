package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.dao.BbTradeExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbTradeExtService;
import com.hp.sh.expv3.bb.extension.util.CommonDateUtils;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public List<BbTradeVo> queryTradeList(Long userId, String asset, String symbol, Integer count,Long startTime,Long endTime) {
        return bbTradeExtMapper.queryTradeList(userId,asset,symbol,count,startTime,endTime);
    }

    @Override
    public BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long endTime) {
         LocalDate localDate = LocalDate.now();
         Long startTime = CommonDateUtils.localDateToTimestamp(localDate);
        return bbTradeExtMapper.queryLastTradeByLtTime(asset,symbol,startTime,endTime);
    }

    @Override
    public List<BbTradeVo> queryLastTrade(String asset, String symbol, Integer count,Long startTime,Long endTime) {
        Map<String, Object> map=new HashMap<>();
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("tradeTimeBegin",startTime);
        map.put("tradeTimeEnd",endTime);
        map.put("orderBy","trade_time");
        map.put("limit",count);
         List<BbTradeVo> list = bbTradeExtMapper.queryLastTrade(map);
        if(CollectionUtils.isEmpty(list)){
            return Lists.emptyList();
        }
        return list;
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
