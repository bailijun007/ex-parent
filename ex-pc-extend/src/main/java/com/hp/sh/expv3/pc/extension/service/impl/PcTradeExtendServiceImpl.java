package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcTradeDAO;
import com.hp.sh.expv3.pc.extension.service.PcTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcTradeExtendServiceImpl implements PcTradeExtendService {
    @Autowired
    private PcTradeDAO pcTradeDAO;

    @Override
    public List<PcTradeVo> queryTradeResult(String asset, String symbol, Integer count) {
        Map<String, Object> map=new HashMap<>();
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderBy","id");
        map.put("limit",count);
        List<PcTradeVo> list = pcTradeDAO.queryList(map);
        return list;
    }

    @Override
    public List<PcTradeVo> queryTradeByGtTime(String asset, String symbol, Long startTime, Long endTime, Integer type) {

        Map<String, Object> map=new HashMap<>();
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("type",type);
        map.put("createdBegin",startTime);
        map.put("createdEnd",endTime);
        List<PcTradeVo> list = pcTradeDAO.queryTradeByGtTime(map);
        return list;
    }

    @Override
    public PcTradeVo queryLastTrade(String asset, String symbol, Long startTime) {
        PcTradeVo pcTradeVo = pcTradeDAO.queryLastTrade(asset,symbol,startTime);
        return pcTradeVo;
    }

    @Override
    public List<PcTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime,Long userId) {
        List<PcTradeVo> list = pcTradeDAO.selectTradeListByTimeInterval(asset,symbol,startTime,endTime,userId);
        return list;
    }
}
