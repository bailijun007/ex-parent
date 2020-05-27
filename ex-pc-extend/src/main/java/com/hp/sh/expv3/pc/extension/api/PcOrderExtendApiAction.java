package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.extension.constant.ExtCommonConstant;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.service.impl.PcAccountExtendServiceImpl;
import com.hp.sh.expv3.pc.extension.util.CommonDateUtils;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.pc.strategy.data.OrderTrade;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.Precision;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@RestController
public class PcOrderExtendApiAction implements PcOrderExtendApi {
    private static final Logger logger = LoggerFactory.getLogger(PcOrderExtendApiAction.class);

    @Autowired
    private PcAccountExtendServiceImpl pcAccountExtendService;

    @Autowired
    private PcOrderExtendService pcOrderExtendService;

    @Autowired
    private PcPositionExtendService pcPositionExtendService;

    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;

    @Autowired
    private PcStrategyContext positionStrategyContext;

    @Override
    public List<UserOrderVo> queryOrderList(Long userId, String asset, String symbol, Long gtOrderId, Long ltOrderId, Integer count, String status, String startTime, String endTime) {
        logger.info("进入pc查询委托接口，参数为：userId={},asset={},symbol={},gtOrderId={},ltOrderId={},count={},status={},startTime={},endTime={}", userId, asset, symbol, gtOrderId, ltOrderId, count, status, startTime, endTime);
        List<UserOrderVo> result = new ArrayList<>();
        List<String> assetList = null;
        List<String> symbolList = null;
        List<Integer> statusList = null;

        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];

        if (null == userId || count == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        //如果同时传了gtOrderId和ltOrderId 则以gtOrderId为查询条件，同时不传，则查全部
        if (gtOrderId != null && ltOrderId != null) {
            ltOrderId = null;
        }

        if (StringUtils.isNotEmpty(asset)) {
            assetList = Arrays.asList(asset.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(symbol)) {
            symbolList = Arrays.asList(symbol.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        }
        if (StringUtils.isNotEmpty(status)) {
            statusList = Arrays.asList(status.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
        }
        List<PcOrderVo> list = pcOrderExtendService.queryOrderList(userId, assetList, symbolList, gtOrderId, ltOrderId, count, statusList, startTime, endTime);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        convertOrderList2(result, list, startTime, endTime);
        return result;
    }


    @Override
    public PageResult<UserOrderVo> queryUserActivityOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Integer isTotalNumber, Integer currentPage, Integer pageSize, Long lastOrderId, Integer nextPage, String startTime, String endTime) {
        logger.info("进入获取当前用户活动委托接口，参数为：userId={},asset={},symbol={},orderType={},longFlag={},closeFlag={},isTotalNumber={},currentPage={},pageSize={},lastOrderId={},nextPage={},startTime={},endTime={}",userId,asset,symbol, orderType,longFlag,closeFlag,isTotalNumber,currentPage,pageSize,lastOrderId,nextPage,startTime,endTime);

        if (StringUtils.isEmpty(asset) ||StringUtils.isEmpty(symbol) || null == userId || currentPage == null || pageSize == null || nextPage == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        PageResult<UserOrderVo> result = new PageResult<>();
        List<UserOrderVo> list = new ArrayList<>();
        PageResult<PcOrderVo> voList = pcOrderExtendService.queryActivityOrder(userId, asset, symbol, orderType, longFlag, closeFlag, lastOrderId, currentPage, pageSize, nextPage, isTotalNumber, startTime, endTime);
        convertOrderList(userId, asset, symbol, list, voList.getList(),startTime,endTime);

        result.setList(list);
        result.setRowTotal(voList.getRowTotal());
        result.setPageNo(currentPage);
        return result;
    }


    /**
     * 转换成结果集返回
     *
     * @param list   最终返回的结果集
     * @param voList 需要转换的list集合
     */
    private void convertOrderList2(List<UserOrderVo> list, List<PcOrderVo> voList, String startTime, String endTime) {
        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        for (PcOrderVo orderVo : voList) {
            UserOrderVo vo = new UserOrderVo();
            BeanUtils.copyProperties(orderVo, vo);
            vo.setFee(orderVo.getFeeCost());
            vo.setQty(orderVo.getVolume());
            vo.setLongFlag(orderVo.getLongFlag());
            vo.setCtime(orderVo.getCreated());

            List<PcOrderTradeVo> orderTradeVoList = pcOrderTradeService.queryOrderTrade(orderVo.getUserId(), orderVo.getAsset(), orderVo.getSymbol(), orderVo.getId() + "", CommonDateUtils.stringToTimestamp(startTime), CommonDateUtils.stringToTimestamp(endTime));
            List<OrderTrade> ots = new ArrayList<>();
            for (PcOrderTradeVo pcOrderTradeVo : orderTradeVoList) {
                OrderTrade ot = new OrderTrade() {
                    @Override
                    public BigDecimal getVolume() {
                        return pcOrderTradeVo.getVolume();
                    }

                    @Override
                    public BigDecimal getPrice() {
                        return pcOrderTradeVo.getPrice();
                    }
                };
                ots.add(ot);
            }
            BigDecimal pnl = pcOrderTradeService.getOrderRealisedPnl(orderVo, orderTradeVoList);
            //成交均价
//            BigDecimal meanPrice = positionStrategyContext.calcOrderMeanPrice(orderVo.getAsset(), orderVo.getSymbol(), orderVo.getLongFlag(), ots);
            vo.setAvgPrice(orderVo.getTradeMeanPrice());
            vo.setFilledQty(orderVo.getFilledVolume());
            vo.setCloseFlag(orderVo.getCloseFlag());
            vo.setTradeRatio(orderVo.getFilledVolume().divide(orderVo.getVolume(), Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros());
            vo.setOrderType(orderVo.getOrderType());
            vo.setClientOid(orderVo.getClientOrderId());
            vo.setSymol(orderVo.getSymbol());
            vo.setRealisedPnl(pnl);
            list.add(vo);
        }

    }

    /**
     * 转换成结果集返回
     *
     * @param userList  最终返回的结果集
     * @param orderList 需要转换的list集合
     */
    private void convertOrderList(Long userId, String asset, String symbol, List<UserOrderVo> userList, List<PcOrderVo> orderList, String startTime, String endTime) {
        if (!CollectionUtils.isEmpty(orderList)) {

            // 获取到所有入参集合的委托Id列表，作为批量查询的入参
            List<Long> orderIds = orderList.stream().map(PcOrderVo::getId).collect(Collectors.toList());

            // 查询多个委托对应的成交记录(order trade)
            List<PcOrderTradeVo> orderTradeList = pcOrderTradeService.listOrderTrade(userId, asset, symbol, orderIds,startTime,endTime);

            Map<Long, List<PcOrderTradeVo>> orderId2OrderTradeList = new HashMap<>();

            for (PcOrderTradeVo pcOrderTradeVo : orderTradeList) {
                List<PcOrderTradeVo> orderTradeVoList = orderId2OrderTradeList.get(pcOrderTradeVo.getOrderId());
                if (null == orderTradeVoList) {
                    orderTradeVoList = new ArrayList<>();
                    orderId2OrderTradeList.put(pcOrderTradeVo.getOrderId(), orderTradeVoList);
                }
                orderTradeVoList.add(pcOrderTradeVo);
            }

            for (PcOrderVo orderVo : orderList) {
                UserOrderVo vo = new UserOrderVo();
                BeanUtils.copyProperties(orderVo, vo);
                vo.setFee(orderVo.getFeeCost().negate());
                vo.setQty(orderVo.getVolume());
                vo.setLongFlag(orderVo.getLongFlag());
                vo.setCtime(orderVo.getCreated());


//                BigDecimal meanPrice = positionStrategyContext.calcOrderMeanPrice(orderVo.getAsset(), orderVo.getSymbol(), orderVo.getLongFlag(),
//                        orderId2OrderTradeList.getOrDefault(orderVo.getId(), Collections.emptyList()));

                BigDecimal pnl = pcOrderTradeService.getOrderRealisedPnl(orderVo, orderId2OrderTradeList.get(orderVo.getId()));
                //成交均价
                vo.setAvgPrice(orderVo.getTradeMeanPrice());
                vo.setFilledQty(orderVo.getFilledVolume());
                vo.setCloseFlag(orderVo.getCloseFlag());
                vo.setTradeRatio(orderVo.getFilledVolume().divide(orderVo.getVolume(), Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros());
                vo.setOrderType(orderVo.getOrderType());
                vo.setClientOid(orderVo.getClientOrderId());
                vo.setSymol(orderVo.getSymbol());
                vo.setRealisedPnl(pnl);
                userList.add(vo);
            }
        }
    }


    /**
     * 获取当前用户历史委托
     *
     * @param userId
     * @param asset
     * @param symbol
     * @param orderType
     * @param longFlag
     * @param closeFlag
     * @param currentPage
     * @param pageSize
     * @param lastOrderId
     * @param nextPage
     * @return
     */
    @Override
    public List<UserOrderVo> queryHistory(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Integer currentPage, Integer pageSize, Long lastOrderId, Integer nextPage, String startTime, String endTime) {
        logger.info("进入获取当前用户历史委托接口，参数为：userId={},asset={},symbol={},orderType={},longFlag={},closeFlag={},currentPage={},pageSize={},lastOrderId={},nextPage={},startTime={},endTime={}",userId,asset,symbol,orderType,longFlag,closeFlag,currentPage,pageSize,lastOrderId,nextPage,startTime,endTime);

        this.checkParam(userId, asset, currentPage, pageSize, nextPage, symbol);
        List<UserOrderVo> result = new ArrayList<>();
        PageResult<PcOrderVo> list = null;
        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        if (lastOrderId == null) {
            List<PcOrderVo> voList = pcOrderExtendService.queryHistory(userId, asset, symbol, orderType, longFlag, closeFlag, null, pageSize, startTime, endTime);
            convertOrderList(userId, asset, symbol, result, voList,startTime,endTime);
        } else {
            list = pcOrderExtendService.queryHistoryOrders(userId, asset, symbol, orderType, longFlag, closeFlag, lastOrderId, currentPage, pageSize, nextPage, null, startTime, endTime);
            convertOrderList(userId, asset, symbol, result, list.getList(),startTime,endTime);
        }
        return result;
    }


    /**
     * 获取当前用户所有委托
     *
     * @param userId
     * @param asset
     * @param symbol
     * @param status
     * @param longFlag
     * @param closeFlag
     * @param currentPage
     * @param pageSize
     * @param lastOrderId
     * @param nextPage
     * @return
     */
    @Override
    public PageResult<UserOrderVo> queryAll(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Integer currentPage, Integer pageSize, Long lastOrderId, Integer nextPage,String startTime,String endTime) {
        logger.info("进入获取当前用户所有委托接口，参数为：userId={},asset={},symbol={},status={},longFlag={},closeFlag={},currentPage={},pageSize={},lastOrderId={},nextPage={},startTime={},endTime={}",userId,asset,symbol,status,longFlag,closeFlag,currentPage,pageSize,lastOrderId,nextPage,startTime,endTime);

        this.checkParam(userId, asset, currentPage, pageSize, nextPage, symbol);
        PageResult<UserOrderVo> pageResult = new PageResult<UserOrderVo>();
        List<UserOrderVo> list = new ArrayList<>();
        PageResult<PcOrderVo> voPageResult = null;
        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        if (lastOrderId == null) {
            voPageResult = pcOrderExtendService.queryAll(userId, asset, symbol, status, longFlag, closeFlag, lastOrderId, pageSize, ExtCommonConstant.IS_PAGE_YES,startTime,endTime);
        } else {
            voPageResult = pcOrderExtendService.queryAllOrders(userId, asset, symbol, status, longFlag, closeFlag, lastOrderId, currentPage, pageSize, nextPage, ExtCommonConstant.IS_PAGE_YES,startTime,endTime);
        }
        convertOrderList(userId, asset, symbol, list, voPageResult.getList(),startTime,endTime);
        pageResult.setList(list);
        pageResult.setRowTotal(voPageResult.getRowTotal());
        pageResult.setPageNo(currentPage);

        return pageResult;
    }

    private String[] getStartAndEndTime(String startTime, String endTime) {
        if (StringUtils.isEmpty(startTime)) {
            startTime = getDefaultDateTime(startTime);
            String[] split = startTime.split("-");
            int day = Integer.parseInt(split[2]) + 1;
            endTime = split[0] + "-" + split[1] + "-" + day;
        }
        String[] startAndEndTime = {startTime, endTime};
        return startAndEndTime;
    }

    @Override
    public PageResult<UserOrderVo> pageQueryOrderList(Long userId, String asset, String symbol, Integer status, Integer closeFlag, Long orderId, Integer pageNo, Integer pageSize, String startTime, String endTime) {
        logger.info("进入订单列表查询(后台admin接口)，参数为：userId={},asset={},symbol={},status={},closeFlag={},pageNo={},pageSize={},startTime={},endTime={}",userId,asset,symbol,status,closeFlag,pageNo,pageSize,startTime,endTime);

        if (pageNo == null || pageSize == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];

        PageResult<UserOrderVo> result = pcOrderExtendService.pageQueryOrderList(userId, asset, symbol, status, closeFlag, orderId, pageNo, pageSize,startTime,endTime);
        if (!CollectionUtils.isEmpty(result.getList())) {
            for (UserOrderVo orderVo : result.getList()) {
                List<PcOrderTradeVo> orderTradeVoList = pcOrderTradeService.queryOrderTrade(orderVo.getUserId(), orderVo.getAsset(), orderVo.getSymol(), String.valueOf(orderVo.getId()), CommonDateUtils.stringToTimestamp(startTime), CommonDateUtils.stringToTimestamp(endTime));
                BigDecimal pnl = pcOrderTradeService.getOrderRealisedPnl(IntBool.isTrue(orderVo.getCloseFlag().intValue()), orderTradeVoList);
                orderVo.setAvgPrice(orderVo.getTradeMeanPrice());
                orderVo.setRealisedPnl(pnl);
            }
        }

        return result;
    }

    @Override
    public BigDecimal queryTotalFee(Long startTime, Long endTime, String asset, String symbol) {
        if (startTime == null || endTime == null||StringUtils.isEmpty(asset)||StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        return pcOrderExtendService.queryTotalFee(startTime, endTime,asset,symbol);
    }

    @Override
    public BigDecimal queryTotalOrder(Long startTime, Long endTime, String asset, String symbol) {
        if (startTime == null || endTime == null||StringUtils.isEmpty(asset)||StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        return pcOrderExtendService.queryTotalOrder(startTime, endTime,asset,symbol);
    }


    private void checkParam(Long userId, String asset, Integer currentPage, Integer pageSize, Integer nextPage, String symbol) {
        if (StringUtils.isEmpty(asset) || null == userId || currentPage == null || pageSize == null || nextPage == null || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
    }

    private String getDefaultDateTime(String startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        //如果开始时间，结束时间没有值则给默认今天时间
        if (org.springframework.util.StringUtils.isEmpty(startTime)) {
            startTime = formatter.format(dateTime);
        }
        return startTime;
    }


}
