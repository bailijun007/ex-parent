package com.hp.sh.expv3.bb.trade.service.impl;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.trade.dao.BbMatchExtMapper;
import com.hp.sh.expv3.bb.trade.service.BbTradeExtendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/5/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbTradeExtendServiceImpl implements BbTradeExtendService {
  @Autowired
   private BbMatchExtMapper pcTradeDAO;

    @Override
    public List<BbTradeVo> queryTradeResult(String asset, String symbol, Integer count) {
        Map<String, Object> map=new HashMap<>();
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderBy","trade_time");
        map.put("limit",count);
        List<BbTradeVo> list = pcTradeDAO.queryList(map);
        return list;
    }

    @Override
    public List<BbTradeVo> queryTradeByGtTime(String asset, String symbol, Long startTime, Long endTime, Integer type) {
        return null;
    }

    @Override
    public BbTradeVo queryLastTrade(String asset, String symbol, Long startTime) {
        return null;
    }

    @Override
    public List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime, Long userId) {
        return null;
    }
}
