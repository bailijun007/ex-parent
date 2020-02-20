package com.hp.sh.expv3.bb.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.gitee.hupadev.commons.bean.BeanHelper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.bb.extension.dao.BbOrderExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.bb.extension.vo.HistoryOrderVo;
//import com.hp.sh.expv3.bb.strategy.common.BbCommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.vo.OrderTradeVo;
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
//    @Autowired
//    private BbCommonOrderStrategy orderStrategy;

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
    public List<HistoryOrderVo> queryHistoryOrderList(Long userId, String asset, String symbol) {
        List<HistoryOrderVo> result = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("activeFlag", IntBool.NO);
        List<BbOrderVo> list = bbOrderExtMapper.queryList(map);
        if (list == null || list.isEmpty()) {
            return result;
        }
        List<Long> orderIdList = BeanHelper.getDistinctPropertyList(list, "id");
        List<OrderTradeVo> _tradeList = bbOrderExtMapper.queryOrderTrade(userId, orderIdList);
        Map<Long, List<OrderTradeVo>> tradeListMap = BeanHelper.groupByProperty(_tradeList, "orderId");

        for (BbOrderVo order : list) {
            HistoryOrderVo historyOrderVo = new HistoryOrderVo();
            BeanUtils.copyProperties(order, historyOrderVo);
            historyOrderVo.setLeverage(order.getLeverage());
            historyOrderVo.setFilledVolume(order.getFilledVolume());
            historyOrderVo.setFilledRatio(order.getFilledVolume().divide(order.getVolume(), Precision.COMMON_PRECISION, Precision.LESS));

            List<OrderTradeVo> orderTradeList = tradeListMap.get(order.getId());
//            BigDecimal meanPrice = orderStrategy.calcOrderMeanPrice(order.getAsset(), order.getSymbol(), orderTradeList);

//            historyOrderVo.setMeanPrice(meanPrice);
            historyOrderVo.setPrice(order.getPrice());
            historyOrderVo.setFeeCost(order.getFeeCost());
            historyOrderVo.setStatus(order.getStatus());

            result.add(historyOrderVo);
        }
        return result;

    }

    @Override
    public BigDecimal getLockAsset(Long userId, String asset) {
        BigDecimal lock=  bbOrderExtMapper.getLockAsset(userId,asset);
        return lock;
    }
}
