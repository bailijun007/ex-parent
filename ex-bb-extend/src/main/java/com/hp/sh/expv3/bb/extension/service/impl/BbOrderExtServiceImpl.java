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
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;
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
        List<BbOrderVo> list = new ArrayList<>();
        //        PageHelper.startPage(pageNo, pageSize);
        long begin = CommonDateUtils.stringToTimestamp(startTime);
        long end = CommonDateUtils.stringToTimestamp(endTime);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("createdBegin", begin);
        map.put("createdEnd", end);

        List<Integer> timeDifference = CommonDateUtils.getTimeDifference(startTime, endTime);
        String tableName = null;

        for (Integer date : timeDifference) {
            //tableName   eg:bb_order_history_usdt__bys_usdt_202005
            tableName = BbExtCommonConstant.BB_ORDER_HISTORY_PREFIX + asset.toLowerCase() +"__"+ symbol.toLowerCase() + "_" + date;
            int existTable = bbOrderExtMapper.existTable(BbExtCommonConstant.DB_NAME_EXPV3_BB, tableName);
            //表不存在则直接跳过
            if (existTable != 1) {
                continue;
            }
            map.put("tableName", tableName);
            List<BbOrderVo> bbOrderVos = bbOrderExtMapper.queryList(map);
            if (!CollectionUtils.isEmpty(bbOrderVos)) {
                list.addAll(bbOrderVos);
            }

        }

        List<BbOrderVo> pageList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
        pageResult.setList(pageList);
        Integer rowTotal = list.size();
        pageResult.setPageNo(pageNo);
        pageResult.setRowTotal(Long.parseLong(String.valueOf(rowTotal)));
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }

    @Override
    public PageResult<BbHistoryOrderVo> queryHistoryOrderList(Long userId, String asset, String symbol, Integer bidFlag, Integer pageSize, Long lastOrderId, Integer nextPage,String startTime, String endTime) {
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

        List<Integer> timeDifference = CommonDateUtils.getTimeDifference(startTime, endTime);
        String tableName = null;

        for (Integer date : timeDifference) {
            //tableName   eg:bb_order_history_usdt__bys_usdt_202005
            tableName = BbExtCommonConstant.BB_ORDER_HISTORY_PREFIX + asset.toLowerCase() +"__"+ symbol.toLowerCase() + "_" + date;
            int existTable = bbOrderExtMapper.existTable(BbExtCommonConstant.DB_NAME_EXPV3_BB, tableName);
            //表不存在则直接跳过
            if (existTable != 1) {
                continue;
            }
            map.put("tableName", tableName);
            List<BbOrderVo> list = null;
            if (lastOrderId == null) {
                list = bbOrderExtMapper.queryHistoryOrderList(map);
            } else {
                map.put("nextPage", nextPage);
                list = bbOrderExtMapper.queryHistoryByIsNextPage(map);
            }
            if (list == null || list.isEmpty()) {
                continue;
            }
            //TODO 可能有bug 待测试
            this.convertOrderList(userId, listResult, list);
        }

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
        List<Long> orderIdList = BeanHelper.getDistinctPropertyList(list, "id");
        List<BbOrderTradeVo> _tradeList = bbOrderTradeExtService.queryOrderTrade(userId, orderIdList);
        Map<Long, List<BbOrderTradeVo>> tradeListMap = BeanHelper.groupByProperty(_tradeList, "orderId");

        for (BbOrderVo order : list) {
            BbHistoryOrderVo historyOrderVo = new BbHistoryOrderVo();
            BeanUtils.copyProperties(order, historyOrderVo);
            historyOrderVo.setVolume(order.getVolume());
            historyOrderVo.setLeverage(order.getLeverage());
            historyOrderVo.setFilledVolume(order.getFilledVolume());
            historyOrderVo.setFilledRatio(order.getFilledVolume().divide(order.getVolume(), Precision.COMMON_PRECISION, Precision.LESS));

            List<BbOrderTradeVo> orderTradeList = tradeListMap.get(order.getId());
            BigDecimal meanPrice = orderStrategy.calcOrderMeanPrice(order.getAsset(), order.getSymbol(), orderTradeList);

            historyOrderVo.setMeanPrice(meanPrice);

            historyOrderVo.setPrice(order.getPrice());
            historyOrderVo.setFeeCost(order.getFeeCost());
            historyOrderVo.setStatus(order.getStatus());
            result.add(historyOrderVo);
        }
    }

    @Override
    public List<BbHistoryOrderVo> queryOrderList(Long userId, List<String> assetList, List<String> symbolList, Long gtOrderId, Long ltOrderId, Integer count, List<Integer> statusList) {
        List<BbHistoryOrderVo> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("assetList", assetList);
        map.put("symbolList", symbolList);
        map.put("statusList", statusList);
        map.put("gtOrderId", gtOrderId);
        map.put("ltOrderId", ltOrderId);
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
    public BigDecimal queryTotalFee(Long startTime, Long endTime) {
        BigDecimal total = bbOrderExtMapper.queryTotalFee(startTime, endTime);
        if (null == total) {
            return BigDecimal.ZERO;
        }
        return total;
    }

    @Override
    public BigDecimal queryTotalOrder(Long startTime, Long endTime) {
        BigDecimal total = bbOrderExtMapper.queryTotalOrder(startTime, endTime);
        return total;
    }
}
