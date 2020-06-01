package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.util.CommonDateUtils;
import com.hp.sh.expv3.pc.extension.vo.*;
import com.hp.sh.expv3.utils.IntBool;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

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
    public BigDecimal getOrderRealisedPnl(List<PcOrderTradeVo> orderTrades) {
        if (null == orderTrades) {
            return BigDecimal.ZERO;
        } else {
            return orderTrades.stream()
                    .filter(Objects::nonNull)
                    .filter(ot -> Objects.nonNull(ot.getPnl()))
                    .map(PcOrderTradeVo::getPnl)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }

    @Override
    public BigDecimal getOrderRealisedPnl(PcOrderVo order, List<PcOrderTradeVo> orderTrades) {
        if (null == order) {
            return BigDecimal.ZERO;
        } else {
            return getOrderRealisedPnl(IntBool.isTrue(order.getCloseFlag().intValue()), orderTrades);
        }
    }

    @Override
    public BigDecimal getOrderRealisedPnl(boolean closeFlag, List<PcOrderTradeVo> orderTrades) {
        if (closeFlag) {
            return getOrderRealisedPnl(orderTrades);
        }
        return BigDecimal.ZERO;
    }

    @Override
    public List<PcOrderTradeVo> queryOrderTrade(Long userId, String asset, String symbol, String orderId, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("orderId", orderId);
        map.put("tradeTimeBegin", startTime);
        map.put("tradeTimeEnd", endTime);
        List<PcOrderTradeVo> voList = pcOrderTradeDAO.queryList(map);
        return voList;
    }

    @Override
    public List<PcOrderTradeVo> listOrderTrade(Long userId, String asset, String symbol, List<Long> orderIds, String startTime, String endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("orderIds", orderIds);
        map.put("tradeTimeBegin", CommonDateUtils.stringToTimestamp(startTime));
        map.put("tradeTimeEnd", CommonDateUtils.stringToTimestamp(endTime));
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
    public List<PcOrderTradeVo> listPcOrderTrade(List<Long> refIds, String asset, String symbol, Long userId, Long startDate, Long endDate) {
        Map<String, Object> map = new HashMap<>();
        map.put("idList", refIds);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
        map.put("tradeTimeBegin", startDate);
        map.put("tradeTimeEnd", endDate);
        return pcOrderTradeDAO.queryList(map);
    }

    @Override
    public List<PcOrderTradeVo> queryTradeRecords(List<String> assetList, List<String> symbolList, Long gtTradeId, Long ltTradeId, Integer count, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("assetList", assetList);
        map.put("symbolList", symbolList);
        map.put("gtTradeId", gtTradeId);
        map.put("ltTradeId", ltTradeId);
        map.put("limit", count);
        map.put("tradeTimeBegin", startTime);
        map.put("tradeTimeEnd", endTime);
        List<PcOrderTradeVo> voList = pcOrderTradeDAO.queryTradeRecords(map);

        return voList;
    }

    @Override
    public PcOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("tradeTimeEnd", statTime);
        LocalDate localDate = Instant.ofEpochMilli(statTime).atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate minusDays = localDate.minusDays(1);
        Long tradeTimeBegin = CommonDateUtils.localDateToTimestamp(minusDays);
        map.put("tradeTimeBegin", tradeTimeBegin);
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

    @Override
    public List<PcOrderTradeExtendVo> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId) {
        List<PcOrderTradeExtendVo> list = pcOrderTradeDAO.selectTradeListByUserId(asset, symbol, startTime, endTime, userId);
        return list;
    }

    @Override
    public BigDecimal queryPcTradeFee(Long userId, String asset, Integer makerFlag, Long beginTime, Long endTime) {
        return pcOrderTradeDAO.queryPcTradeFee(userId, asset, makerFlag, beginTime, endTime);
    }

    @Override
    public List<PcOrderTradeDetailVo> queryHistory(Long userId, String asset, String symbol, Long lastTradeId, Integer nextPage, Integer pageSize, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("tradeTimeBegin", startTime);
        map.put("tradeTimeEnd", endTime);
        map.put("limit", pageSize);
        List<PcOrderTradeDetailVo> list = pcOrderTradeDAO.queryHistory(map);
        if (!CollectionUtils.isEmpty(list)) {
            for (PcOrderTradeDetailVo pcOrderTradeDetailVo : list) {
                pcOrderTradeDetailVo.setAmt(pcOrderTradeDetailVo.getPrice().multiply(pcOrderTradeDetailVo.getQty()));
            }
        }

        return list;
    }


}
