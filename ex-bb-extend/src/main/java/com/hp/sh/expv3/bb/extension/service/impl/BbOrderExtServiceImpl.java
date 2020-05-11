package com.hp.sh.expv3.bb.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.gitee.hupadev.commons.bean.BeanHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.bb.extension.constant.BbExtCommonConstant;
import com.hp.sh.expv3.bb.extension.constant.OrderStatus;
import com.hp.sh.expv3.bb.extension.dao.BbOrderExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.strategy.common.BBExtCommonOrderStrategy;
import com.hp.sh.expv3.bb.extension.util.CommonDateUtils;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import com.hp.sh.expv3.bb.extension.vo.BbHistoryOrderVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.commons.page.ExPage;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbOrderExtServiceImpl implements BbOrderExtService {

    @Autowired
    private BbOrderExtMapper bbOrderExtMapper;

    @Autowired
    private BbOrderTradeExtService bbOrderTradeExtService;

    @Autowired
    private BBExtCommonOrderStrategy orderStrategy;

    @Override
    public PageResult<BbOrderVo> queryAllBbOrederHistory(Long userId, String asset, String symbol, String startTime, String endTime, Integer pageNo, Integer pageSize) {
        PageResult<BbOrderVo> pageResult = new PageResult<>();
        List<BbOrderVo> list = null;
        long begin = CommonDateUtils.stringToTimestamp(startTime);
        long end = CommonDateUtils.stringToTimestamp(endTime);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("createdBegin", begin);
        map.put("createdEnd", end);

        Long count=bbOrderExtMapper.queryCount(map);
        map.put("offset", pageNo-1);
        map.put("limit", pageSize);
        list = bbOrderExtMapper.queryList(map);
        Integer rowTotal =count.intValue();
        pageResult.setList(list);
        pageResult.setPageNo(pageNo);
        pageResult.setRowTotal(count);
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }

    @Override
    public PageResult<BbHistoryOrderVo> queryHistoryOrderList(Long userId, String asset, String symbol, Integer bidFlag, Integer pageSize, Long lastOrderId, Integer nextPage, String startTime, String endTime) {
        PageResult<BbHistoryOrderVo> result = new PageResult<>();
        result.setRowTotal(0L);
        List<BbHistoryOrderVo> listResult = new ArrayList<>();
        long begin = CommonDateUtils.stringToTimestamp(startTime);
        long end = CommonDateUtils.stringToTimestamp(endTime);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("bidFlag", bidFlag);
        map.put("activeFlag", IntBool.NO);
        map.put("lastOrderId", lastOrderId);
        map.put("createdBegin", begin);
        map.put("createdEnd", end);
        map.put("limit", pageSize);

//        List<Integer> timeDifference = CommonDateUtils.getTimeDifference(startTime, endTime);
//        String tableName = null;
//
//        for (Integer date : timeDifference) {
//            //tableName   eg:bb_order_history_usdt__bys_usdt_202005
//            tableName = BbExtCommonConstant.BB_ORDER_HISTORY_PREFIX + asset.toLowerCase() + "__" + symbol.toLowerCase() + "_" + date;
//            int existTable = bbOrderExtMapper.existTable(BbExtCommonConstant.DB_NAME_EXPV3_BB, tableName);
//            //表不存在则直接跳过
//            if (existTable != 1) {
//                continue;
//            }
//            map.put("tableName", tableName);
//
//            //////
//        }

        List<BbOrderVo> list = null;
        if (lastOrderId == null) {
            list = bbOrderExtMapper.queryHistoryOrderList(map);
        } else {
            map.put("nextPage", nextPage);
            list = bbOrderExtMapper.queryHistoryByIsNextPage(map);
        }
        if (list == null || list.isEmpty()) {
            return null;
        }
        this.convertOrderList(userId, listResult, list);

//        List<BbHistoryOrderVo> listByLimit = listResult.stream().limit(pageSize).collect(Collectors.toList());
        result.setList(listResult);
        return result;

    }


    @Override
    public BigDecimal getLockAsset(Long userId, String asset) {
        BigDecimal lock = bbOrderExtMapper.getLockAsset(userId, asset);
        if (null == lock) {
            return BigDecimal.ZERO;
        }
        return lock;
    }

    @Override
    public PageResult<BbHistoryOrderVo> queryBbActiveOrderList(Long userId, String asset, String symbol, Integer bidFlag, Integer pageSize, Long lastOrderId, Integer nextPage) {
        PageResult<BbHistoryOrderVo> result = new PageResult<>();
        List<BbHistoryOrderVo> voList = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("bidFlag", bidFlag);
//        map.put("activeFlag", IntBool.YES);
        map.put("lastOrderId", lastOrderId);

        //总条数
        Long count = bbOrderExtMapper.queryActiveOrderCount(map);

        map.put("limit", pageSize);
        List<BbOrderVo> list = null;
        if (lastOrderId == null) {
            list = bbOrderExtMapper.queryBbActiveOrderList(map);
        } else {
            map.put("nextPage", nextPage);
            list = bbOrderExtMapper.queryBbActiveOrdersByIsNextPage(map);
        }

        if (list == null || list.isEmpty()) {
            return result;
        }
        this.convertOrderList(userId, voList, list);
        result.setList(voList);
        result.setRowTotal(count);
        return result;
    }

    private void convertOrderList(Long userId, List<BbHistoryOrderVo> result, List<BbOrderVo> list) {
//        List<Long> orderIdList = BeanHelper.getDistinctPropertyList(list, "id");
//        List<BbOrderTradeVo> _tradeList = bbOrderTradeExtService.queryOrderTrade(userId, orderIdList);
//        Map<Long, List<BbOrderTradeVo>> tradeListMap = BeanHelper.groupByProperty(_tradeList, "orderId");

        for (BbOrderVo order : list) {
            BbHistoryOrderVo historyOrderVo = new BbHistoryOrderVo();
            BeanUtils.copyProperties(order, historyOrderVo);
            historyOrderVo.setVolume(order.getVolume());
            historyOrderVo.setLeverage(order.getLeverage());
            historyOrderVo.setFilledVolume(order.getFilledVolume());
            historyOrderVo.setFilledRatio(order.getFilledVolume().divide(order.getVolume(), Precision.COMMON_PRECISION, Precision.LESS));

//            List<BbOrderTradeVo> orderTradeList = tradeListMap.get(order.getId());
//            BigDecimal meanPrice = orderStrategy.calcOrderMeanPrice(order.getAsset(), order.getSymbol(), orderTradeList);

            historyOrderVo.setMeanPrice(order.getTradeMeanPrice());
            historyOrderVo.setPrice(order.getPrice());
            historyOrderVo.setFeeCost(order.getFeeCost());
            historyOrderVo.setStatus(order.getStatus());
            result.add(historyOrderVo);
        }
    }

    @Override
    public List<BbHistoryOrderVo> queryOrderList(Long userId, List<String> assetList, List<String> symbolList, Long gtOrderId, Long ltOrderId, Integer count, List<Integer> statusList, String startTime, String endTime) {
        List<BbHistoryOrderVo> result = new ArrayList<>();
        Long begin = CommonDateUtils.stringToTimestamp(startTime);
        Long end = CommonDateUtils.stringToTimestamp(endTime);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("assetList", assetList);
        map.put("symbolList", symbolList);
        map.put("statusList", statusList);
        map.put("gtOrderId", gtOrderId);
        map.put("ltOrderId", ltOrderId);
        map.put("createdBegin", begin);
        map.put("createdEnd", end);
        map.put("limit", count);
        List<BbOrderVo> list = null;
        //如果状态为已取消 或者部分成交，则直接查bb_order表
        if (statusList.contains(OrderStatus.CANCELED) || statusList.contains(OrderStatus.FILLED)) {
            map.put("activeFlag", IntBool.NO);
            list = bbOrderExtMapper.queryOrderList(map);
        } else {
//            map.put("activeFlag", IntBool.YES);
            list = bbOrderExtMapper.queryBbActiveOrders(map);
        }

        if (list == null || list.isEmpty()) {
            return result;
        }

        this.convertOrderList(userId, result, list);
        return result;

    }

    @Override
    public List<BbOrderVo> queryByIds(List<Long> refIds) {

        return bbOrderExtMapper.queryByIds(refIds);
    }

    @Override
    public BigDecimal queryTotalFee(String asset, String symbol,Long startTime, Long endTime) {
        BigDecimal total = bbOrderExtMapper.queryTotalFee(asset,symbol,startTime, endTime);
        if (null == total) {
            return BigDecimal.ZERO;
        }
        return total;
    }

    @Override
    public BigDecimal queryTotalOrder(String asset, String symbol,Long startTime, Long endTime) {
        BigDecimal total = bbOrderExtMapper.queryTotalOrder(asset,symbol,startTime, endTime);
        return total;
    }
}
