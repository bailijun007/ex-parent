package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.constant.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.service.impl.PcAccountExtendServiceImpl;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Integers;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@RestController
public class PcOrderExtendApiAction implements PcOrderExtendApi {
    @Autowired
    private PcAccountExtendServiceImpl pcAccountExtendService;

    @Autowired
    private PcOrderExtendService pcOrderExtendService;

    @Autowired
    private PcPositionExtendService pcPositionExtendService;

    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;

    @Override
    public List<UserOrderVo> queryOrderList(Long userId, String asset, String symbol, Long gtOrderId, Long ltOrderId, Integer count, String status) {
        if (null == userId || count == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        //如果同时传了gtOrderId和ltOrderId 则以gtOrderId为查询条件，同时不传，则查全部
        if (gtOrderId != null && ltOrderId != null) {
            ltOrderId = null;
        }
        List<UserOrderVo> result=new ArrayList<>();
        List<String> assetList = Arrays.asList(asset.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<String> symbolList = Arrays.asList(symbol.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<Integer> statusList = Arrays.asList(status.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
        List<PcOrderVo> list = pcOrderExtendService.queryOrderList(userId, assetList, symbolList, gtOrderId, ltOrderId, count, statusList);
        if (!CollectionUtils.isEmpty(list)) {
            for (PcOrderVo orderVo : list) {
                UserOrderVo vo = getUserOrderVo(orderVo);
                result.add(vo);
            }
        }

        return result;
    }



    @Override
    public PageResult<UserOrderVo> queryUserOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Integer currentPage, Integer pageSize, Integer isTotalNumber) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId || currentPage == null || pageSize == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        PageResult<UserOrderVo> result=new PageResult<>();
        List<UserOrderVo> list = new ArrayList<>();
        List<PcOrderVo> voList = pcOrderExtendService.findCurrentUserOrder(userId, asset, symbol, orderType, longFlag, closeFlag);
        if (!CollectionUtils.isEmpty(voList)) {
            getOrderList(currentPage, pageSize, list, voList);
        }

        result.setList(list);
        if(isTotalNumber==1){
            result.setRowTotal(Long.parseLong(list.size()+""));
        }
        return result;
    }


    @Override
    public List<UserOrderVo> queryHistory(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Integer currentPage, Integer pageSize, Long lastOrderId, Integer nextPage) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId || currentPage == null || pageSize == null || nextPage == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<UserOrderVo> result = new ArrayList<>();
        List<PcOrderVo> list = pcOrderExtendService.queryHistory(userId, asset, symbol, orderType, longFlag, closeFlag, lastOrderId, currentPage, pageSize, nextPage);
        if (!CollectionUtils.isEmpty(list)) {
            for (PcOrderVo orderVo : list) {
                UserOrderVo vo = getUserOrderVo(orderVo);
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public List<UserOrderVo> queryAll(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Integer currentPage, Integer pageSize) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId || currentPage == null || pageSize == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<UserOrderVo> result = new ArrayList<>();
        List<PcOrderVo> list = pcOrderExtendService.queryAll(userId, asset, symbol, status, longFlag, closeFlag);
        if (!CollectionUtils.isEmpty(list)) {
            getOrderList(currentPage, pageSize, result, list);
        }
        for (UserOrderVo pcOrderVo : result) {
            BigDecimal realisedPnl = pcOrderTradeService.getRealisedPnl(pcOrderVo.getId(), pcOrderVo.getUserId());
            pcOrderVo.setRealisedPnl(realisedPnl);
        }

        return result;
    }

    private void getOrderList(Integer currentPage, Integer pageSize, List<UserOrderVo> result, List<PcOrderVo> list) {
        List<PcOrderVo> orderVos = list.stream().skip(pageSize * (currentPage - 1))
                .limit(pageSize)
                .collect(Collectors.toList());

        for (PcOrderVo orderVo : orderVos) {
            UserOrderVo vo = getUserOrderVo(orderVo);
            result.add(vo);
        }
    }

    private UserOrderVo getUserOrderVo(PcOrderVo orderVo) {
        UserOrderVo vo = new UserOrderVo();
        BeanUtils.copyProperties(orderVo, vo);
        vo.setFee(orderVo.getFeeCost());
        vo.setQty(orderVo.getVolume());
        vo.setLongFlag(orderVo.getLongFlag());
        Date created = orderVo.getCreated();
        if (null != created) {
            vo.setCtime(created.getTime());
        }
        //平均价 暂时写死，后期掉老王接口
        vo.setAvgPrice(BigDecimal.ZERO);
        vo.setFilledQty(orderVo.getFilledVolume());
        vo.setCloseFlag(orderVo.getCloseFlag());
        vo.setTradeRatio(orderVo.getFilledVolume().divide(orderVo.getVolume()));
        vo.setOrderType(orderVo.getOrderType());
        vo.setClientOid(orderVo.getClientOrderId());
        return vo;
    }

}
