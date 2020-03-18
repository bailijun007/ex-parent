package com.hp.sh.expv3.bb.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.gitee.hupadev.commons.bean.BeanHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.bb.extension.dao.BbOrderExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.strategy.common.BBExtCommonOrderStrategy;
import com.hp.sh.expv3.bb.extension.vo.BbHistoryOrderVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

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
    public PageResult<BbOrderVo> queryAllBbOrederHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<BbOrderVo> pageResult = new PageResult<>();
        PageHelper.startPage(pageNo, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        List<BbOrderVo> list = bbOrderExtMapper.queryList(map);
        PageInfo<BbOrderVo> info = new PageInfo<>(list);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        pageResult.setList(list);
        return pageResult;
    }

    @Override
    public PageResult<BbHistoryOrderVo> queryHistoryOrderList(Long userId, String asset, String symbol, Integer bidFlag, Integer pageSize, Long lastOrderId, Integer nextPage) {
        PageResult<BbHistoryOrderVo> result=new PageResult<>();
        List<BbHistoryOrderVo> bbHistoryOrderVos = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("bidFlag", bidFlag);
        map.put("activeFlag", IntBool.NO);
        map.put("lastOrderId", lastOrderId);

        result.setRowTotal(0L);

        map.put("limit", pageSize);
        List<BbOrderVo> list = null;
        if (lastOrderId == null) {
            list = bbOrderExtMapper.queryHistoryOrderList(map);
        } else {
            map.put("nextPage", nextPage);
            list = bbOrderExtMapper.queryHistoryByIsNextPage(map);
        }

        if (list == null || list.isEmpty()) {
            return result;
        }
        if (list == null || list.isEmpty()) {
            return result;
        }
        this.convertOrderList(userId, bbHistoryOrderVos, list);

        result.setList(bbHistoryOrderVos);
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
        List<BbHistoryOrderVo> voList=new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("bidFlag", bidFlag);
        map.put("activeFlag", IntBool.YES);
        map.put("lastOrderId", lastOrderId);

        //总条数
      Long count= bbOrderExtMapper.queryCount(map);

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
        List<BbOrderVo> list = bbOrderExtMapper.queryOrderList(map);
        if (list == null || list.isEmpty()) {
            return result;
        }
        this.convertOrderList(userId, result, list);
        return result;

    }
}
