package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.hp.sh.expv3.dev.CrossDB;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.service.impl.PcAccountExtendServiceImpl;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;

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
        List<UserOrderVo> result = new ArrayList<>();
        List<String> assetList = Arrays.asList(asset.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<String> symbolList = Arrays.asList(symbol.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<Integer> statusList = Arrays.asList(status.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
        List<PcOrderVo> list = pcOrderExtendService.queryOrderList(userId, assetList, symbolList, gtOrderId, ltOrderId, count, statusList);
        convertOrderList(result, list);

        return result;
    }


    @Override
    public PageResult<UserOrderVo> queryUserOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Integer isTotalNumber, Integer currentPage, Integer pageSize, Long lastOrderId, Integer nextPage) {
        checkParam(userId, asset, symbol, currentPage, pageSize, nextPage);
        PageResult<UserOrderVo> result = new PageResult<>();
        List<UserOrderVo> list = new ArrayList<>();
        List<PcOrderVo> voList = pcOrderExtendService.queryOrders(userId, asset, symbol, orderType, longFlag, closeFlag, lastOrderId, currentPage, pageSize, nextPage);
        convertOrderList(list, voList);

        result.setList(list);
        if (isTotalNumber == 1) {
            result.setRowTotal(Long.parseLong(list.size() + ""));
        }
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
                vo.setTradeRatio(orderVo.getFilledVolume().divide(orderVo.getVolume()));
                vo.setOrderType(orderVo.getOrderType());
                vo.setClientOid(orderVo.getClientOrderId());
                list.add(vo);
            }
        }
    }


    @Override
    public List<UserOrderVo> queryHistory(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Integer currentPage, Integer pageSize, Long lastOrderId, Integer nextPage) {
        this.checkParam(userId, asset, symbol, currentPage, pageSize, nextPage);
        List<UserOrderVo> result = new ArrayList<>();
        List<PcOrderVo> list = pcOrderExtendService.queryOrders(userId, asset, symbol, orderType, longFlag, closeFlag, lastOrderId, currentPage, pageSize, nextPage);
        convertOrderList(result, list);
        return result;
    }

    @Override
    public List<UserOrderVo> queryAll(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Integer currentPage, Integer pageSize, Long lastOrderId, Integer nextPage) {
        this.checkParam(userId, asset, symbol, currentPage, pageSize, nextPage);
        List<UserOrderVo> result = new ArrayList<>();
        List<PcOrderVo> list = pcOrderExtendService.queryOrders(userId, asset, symbol, status, longFlag, closeFlag, lastOrderId, currentPage, pageSize, nextPage);
        convertOrderList(result, list);
        return result;
    }

    @Override
    @CrossDB
    public PageResult<UserOrderVo> pageQueryOrderList(Long userId, String asset, String symbol, Integer status, Integer closeFlag, Long orderId, Integer pageNo, Integer pageSize) {
        PageResult<UserOrderVo> result = pcOrderExtendService.pageQueryOrderList(userId, asset, symbol, status, closeFlag, orderId, pageNo, pageSize);
        return result;
    }

    private void checkParam(Long userId, String asset, String symbol, Integer currentPage, Integer pageSize, Integer nextPage) {
        if (StringUtils.isEmpty(asset) || null == userId || currentPage == null || pageSize == null || nextPage == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
    }


}
