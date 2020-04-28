package com.hp.sh.expv3.pc.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.pc.extension.constant.ExtCommonConstant;
import com.hp.sh.expv3.pc.extension.constant.OrderStatus;
import com.hp.sh.expv3.pc.extension.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.extension.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.strategy.data.OrderTrade;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcOrderExtendServiceImpl implements PcOrderExtendService {
    @Autowired
    private PcOrderDAO pcOrderDAO;

    @Autowired
    private PcStrategyContext positionStrategyContext;


    @Override
    public BigDecimal getGrossMargin(Long userId, String asset) {
        if (userId == null) {
            return BigDecimal.ZERO;
        }
        return pcOrderDAO.getGrossMargin(userId, asset);
    }

    @Override
    public List<PcOrderVo> orderList(Long closePosId, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("closePosId", closePosId);
        map.put("userId", userId);
//        map.put("limit", null);
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryList(map);
        return pcOrderVos;
    }

    @Override
    public List<PcOrderVo> activeOrderList(Long closePosId, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("closePosId", closePosId);
        map.put("userId", userId);
        map.put("activeFlag", ExtCommonConstant.ACTIVE_FLAG);
        map.put("limit", null);
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryList(map);
        return pcOrderVos;
    }

    @Override
    public List<PcOrderVo> findCurrentUserOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("orderType", orderType);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);
//        map.put("limit", null);
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryList(map);
        return pcOrderVos;
    }

    /**
     * 查询所有委托
     *
     * @param userId
     * @param asset
     * @param symbol
     * @param status
     * @param longFlag
     * @param closeFlag
     * @param lastOrderId
     * @param currentPage
     * @param pageSize
     * @param nextPage
     * @param isTotalNumber
     * @return
     */
    @Override
    public PageResult<PcOrderVo> queryAllOrders(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber) {
        PageResult<PcOrderVo> result = new PageResult<PcOrderVo>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("status", status);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);
        map.put("pageSize", pageSize);

        isPage(lastOrderId, currentPage, pageSize, nextPage, isTotalNumber, result, map);
        return result;
    }

    private void isPage(Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber, PageResult<PcOrderVo> result, Map<String, Object> map) {
        List<PcOrderVo> pcOrderVos = null;
        if (isTotalNumber == null) {
            map.put("lastOrderId", lastOrderId);
            map.put("currentPage", currentPage);
            map.put("nextPage", nextPage);
            pcOrderVos = pcOrderDAO.queryOrders(map);
            result.setList(pcOrderVos);
        } else if (isTotalNumber == 1) {
            Long count = pcOrderDAO.queryCount(map);
            result.setRowTotal(count);
            map.put("limit", pageSize);
            map.put("isTotalNumber", isTotalNumber);
            pcOrderVos = pcOrderDAO.queryOrders(map);
//            List<PcOrderVo> voList = pcOrderVos.stream().skip(pageSize * (currentPage - 1)).limit(pageSize).collect(Collectors.toList());
            result.setList(pcOrderVos);
            result.setPageNo(currentPage);
        }
    }

    @Override
    public PageResult<PcOrderVo> queryHistoryOrders(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber) {
        PageResult<PcOrderVo> result = new PageResult<PcOrderVo>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("status", status);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);
        map.put("activeFlag", IntBool.NO);
        map.put("pageSize", pageSize);
        isPage(lastOrderId, currentPage, pageSize, nextPage, isTotalNumber, result, map);

        return result;
    }

    @Override
    public List<PcOrderVo> queryHistory(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("status", status);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);
        map.put("activeFlag", IntBool.NO);
        map.put("lastOrderId", lastOrderId);
        map.put("limit", pageSize);
        List<PcOrderVo> list = this.pcOrderDAO.queryNextList(map);
        return list;
    }

    @Override
    public PcOrderVo getPcOrder(Long orderId, String asset, String symbol, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", orderId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
        map.put("limit", null);
        PcOrderVo pcOrderVo = pcOrderDAO.queryOne(map);
        return pcOrderVo;
    }

    @Override
    public List<PcOrderVo> queryOrderList(Long userId, List<String> assetList, List<String> symbolList, Long gtOrderId, Long ltOrderId, Integer count, List<Integer> statusList) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("assetList", assetList);
        map.put("symbolList", symbolList);
        map.put("statusList", statusList);
        map.put("gtOrderId", gtOrderId);
        map.put("ltOrderId", ltOrderId);
        map.put("limit", count);

        List<PcOrderVo> list =null;
        //如果状态为已取消 或者部分成交，则直接查bb_order表
        if(statusList.contains(OrderStatus.CANCELED)||statusList.contains(OrderStatus.FILLED)){
            map.put("activeFlag", IntBool.NO);
            list = pcOrderDAO.queryOrderList(map);
        }else {
            map.put("activeFlag", IntBool.YES);
            list = pcOrderDAO.queryPcActivityOrder(map);
        }
        return list;
    }

    @Override
    public PageResult<UserOrderVo> pageQueryOrderList(Long userId, String asset, String symbol, Integer status, Integer closeFlag, Long orderId, Integer pageNo, Integer pageSize) {
        PageResult<UserOrderVo> result = new PageResult<>();
        PageHelper.startPage(pageNo, pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("id", orderId);
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("status", status);
        map.put("closeFlag", closeFlag);
        List<UserOrderVo> list = new ArrayList<>();
        List<PcOrderVo> voList = pcOrderDAO.queryList(map);
        PageInfo<PcOrderVo> info = new PageInfo<>(voList);
        result.setPageNo(info.getPageNum());
        result.setRowTotal(info.getTotal());
        result.setPageCount(info.getPages());
        //转换成结果集返回
        convertOrderList(list, voList);

        result.setList(list);
        return result;
    }

    /**
     * 查询用户活动委托
     *
     * @param userId
     * @param asset
     * @param symbol
     * @param status
     * @param longFlag
     * @param closeFlag
     * @param lastOrderId
     * @param currentPage
     * @param pageSize
     * @param nextPage
     * @param isTotalNumber
     * @return
     */
    @Override
    public PageResult<PcOrderVo> queryUserActivityOrder(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber) {
        PageResult<PcOrderVo> result = new PageResult<PcOrderVo>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("status", status);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);
        map.put("activeFlag", IntBool.YES);

        isPage(lastOrderId, currentPage, pageSize, nextPage, isTotalNumber, result, map);

        return result;
    }

    @Override
    public PageResult<PcOrderVo> queryAll(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer pageSize, Integer isPageYes) {
        PageResult<PcOrderVo> result = new PageResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("status", status);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);

        map.put("lastOrderId", lastOrderId);
        map.put("limit", pageSize);
        List<PcOrderVo> list = this.pcOrderDAO.queryNextList(map);
        result.setRowTotal(Long.parseLong(String.valueOf(list.size())));
        result.setList(list);
        return result;
    }

    @Override
    public PageResult<PcOrderVo> queryActivityOrder(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber) {
        PageResult<PcOrderVo> result = new PageResult<PcOrderVo>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("status", status);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);
        map.put("activeFlag", IntBool.YES);
        map.put("lastOrderId", lastOrderId);
        map.put("limit", pageSize);
        List<PcOrderVo> list = this.pcOrderDAO.queryActivityOrder(map);
        result.setList(list);
        result.setRowTotal(Long.parseLong(String.valueOf(list.size())));
        result.setPageNo(currentPage);
        return result;
    }

    @Override
    public BigDecimal queryTotalFee(Long startTime, Long endTime) {
        BigDecimal total = pcOrderDAO.queryTotalFee(startTime, endTime);
        if (null == total) {
            return BigDecimal.ZERO;
        }
        return total;
    }

    @Override
    public BigDecimal queryTotalOrder(Long startTime, Long endTime) {
        return   pcOrderDAO.queryTotalOrder(startTime, endTime);
    }


    /**
     * 转换成结果集返回
     *
     * @param list   最终返回的结果集
     * @param voList 需要转换的list集合
     */
    private void convertOrderList(List<UserOrderVo> list, List<PcOrderVo> voList) {
        if (!CollectionUtils.isEmpty(voList)) {
            for (PcOrderVo orderVo : voList) {
                UserOrderVo vo = new UserOrderVo();
                BeanUtils.copyProperties(orderVo, vo);
                vo.setFee(orderVo.getFeeCost());
                vo.setQty(orderVo.getVolume());
                vo.setLongFlag(orderVo.getLongFlag());
                vo.setCtime(orderVo.getCreated());
                vo.setFilledQty(orderVo.getFilledVolume());
                vo.setCloseFlag(orderVo.getCloseFlag());
                vo.setTradeRatio(orderVo.getFilledVolume().divide(orderVo.getVolume(), Precision.PERCENT_PRECISION, Precision.LESS).stripTrailingZeros());
                vo.setOrderType(orderVo.getOrderType());
                vo.setClientOid(orderVo.getClientOrderId());
                list.add(vo);
            }
        }
    }
}
