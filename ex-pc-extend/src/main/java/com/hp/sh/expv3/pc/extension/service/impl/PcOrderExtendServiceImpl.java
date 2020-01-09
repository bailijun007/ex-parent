package com.hp.sh.expv3.pc.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.pc.extension.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
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
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcOrderExtendServiceImpl implements PcOrderExtendService {
    @Autowired
    private PcOrderDAO pcOrderDAO;

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
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryList(map);
        return pcOrderVos;
    }

    @Override
    public PageResult<PcOrderVo> queryOrders(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber) {
        PageResult<PcOrderVo> result=new  PageResult<PcOrderVo>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("orderType", orderType);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);

        if(isTotalNumber==null){
            map.put("pageSize", pageSize);
            map.put("lastOrderId", lastOrderId);
            map.put("currentPage", currentPage);
            map.put("nextPage", nextPage);

        }else  if(isTotalNumber==1){
            Long count = pcOrderDAO.queryCount(map);
            result.setRowTotal(count);
        }
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryOrders(map);
        result.setList(pcOrderVos);
        return result;
    }


    @Override
    public PcOrderVo getPcOrder(Long orderId, String asset, String symbol, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", orderId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("userId", userId);
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
        List<PcOrderVo> list = pcOrderDAO.queryOrderList(map);
        return list;
    }

    @Override
    public PageResult<UserOrderVo> pageQueryOrderList(Long userId, String asset, String symbol, Integer status, Integer closeFlag, Long orderId, Integer pageNo, Integer pageSize) {
        PageResult<UserOrderVo> result = new PageResult<>();
        PageHelper.startPage(pageNo,pageSize);
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

    @Override
    public PageResult<PcOrderVo> queryUserActivityOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber) {
        PageResult<PcOrderVo> result=new  PageResult<PcOrderVo>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbol", symbol);
        map.put("orderType", orderType);
        map.put("longFlag", longFlag);
        map.put("closeFlag", closeFlag);

        if(isTotalNumber==null){
            map.put("pageSize", pageSize);
            map.put("lastOrderId", lastOrderId);
            map.put("currentPage", currentPage);
            map.put("nextPage", nextPage);

        }else  if(isTotalNumber==1){
            Long count = pcOrderDAO.queryCount(map);
            result.setRowTotal(count);
        }
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryUserActivityOrder(map);
        result.setList(pcOrderVos);
        return result;
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
                //平均价 暂时写死，后期掉老王接口
                vo.setAvgPrice(BigDecimal.ZERO);
                vo.setFilledQty(orderVo.getFilledVolume());
                vo.setCloseFlag(orderVo.getCloseFlag());
                vo.setTradeRatio(orderVo.getFilledVolume().divide(orderVo.getVolume(), Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros());
                vo.setOrderType(orderVo.getOrderType());
                vo.setClientOid(orderVo.getClientOrderId());
                list.add(vo);
            }
        }
    }
}
