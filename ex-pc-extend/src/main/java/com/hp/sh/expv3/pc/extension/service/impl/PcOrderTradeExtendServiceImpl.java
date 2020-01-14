package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcOrderTradeExtendServiceImpl implements PcOrderTradeExtendService {
    @Autowired
    private PcOrderTradeDAO pcOrderTradeDAO;

    @Override
    public BigDecimal getRealisedPnl(Long posId, Long userId, Long orderId) {
        return pcOrderTradeDAO.getRealisedPnl(posId, userId, null);
    }

    @Override
    public List<PcOrderTradeVo> queryOrderTrade(Long userId, String asset, String symbol, String orderId) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("orderId", orderId);
        List<PcOrderTradeVo> voList = pcOrderTradeDAO.queryList(map);
        return voList;
    }

    @Override
    public List<PcOrderTradeVo> listOrderTrade(Long userId, String asset, String symbol, List<Long> orderIds) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("orderIds", orderIds);
        List<PcOrderTradeVo> voList = pcOrderTradeDAO.queryList(map);
        return voList;
    }

    @Override
    public PcOrderTradeVo getPcOrderTrade(Long refId, String asset, String symbol, Long userId, Long time) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", refId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
        map.put("createdBegin", time);
        PcOrderTradeVo pcOrderTradeVo = pcOrderTradeDAO.queryOne(map);
        return pcOrderTradeVo;
    }

    @Override
    public List<PcOrderTradeVo> listPcOrderTrade(List<Long> refIds, String asset, String symbol, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("idList", refIds);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
        return pcOrderTradeDAO.queryList(map);
    }

    @Override
    public List<PcOrderTradeVo> queryTradeRecords(List<String> assetList, List<String> symbolList, Long gtTradeId, Long ltTradeId, Integer count) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetList", assetList);
        map.put("symbolList", symbolList);
        map.put("gtTradeId", gtTradeId);
        map.put("ltTradeId", ltTradeId);
        map.put("limit", count);
        List<PcOrderTradeVo> voList = pcOrderTradeDAO.queryTradeRecords(map);

        return voList;
    }

    @Override
    public PcOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("tradeTime", statTime);
        PcOrderTradeVo vo = pcOrderTradeDAO.selectLessTimeTrade(map);
        return vo;
    }

    @Override
    public List<PcOrderTradeVo> selectAllTradeListByUser(String asset, String symbol, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
        List<PcOrderTradeVo> list = pcOrderTradeDAO.selectAllTradeListByUser(map);
        return list;
    }


    @Override
    public List<PcOrderTradeVo> queryLastTradeRecord(String asset, String symbol, Integer count) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("limit", count);
        List<PcOrderTradeVo> list = pcOrderTradeDAO.queryLastTradeRecord(map);
        return list;
    }

    @Override
    public List<PcOrderTradeVo> selectPcFeeCollectByAccountId(String asset, String symbol, Long userId, Long statTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
        map.put("tradeTimeBegin", statTime);
        map.put("tradeTimeEnd", endTime);
        List<PcOrderTradeVo> list = pcOrderTradeDAO.selectPcFeeCollectByAccountId(map);
        return list;
    }


}
