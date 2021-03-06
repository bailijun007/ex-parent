package com.hp.sh.expv3.pc.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcOrderExtendService {


    BigDecimal getGrossMargin( Long userId,String asset);

    List<PcOrderVo> activeOrderList(Long closePosId, Long userId);

    PageResult<PcOrderVo> queryAllOrders(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber,Long startTime,Long endTime);

    PageResult<PcOrderVo> queryHistoryOrders(Long userId, String asset, List<String> symbolList, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber, Long startTime, Long endTime);

    List<PcOrderVo> queryHistory(Long userId, String asset, List<String> symbolList, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer pageSize, Long startTime, Long endTime);

    PcOrderVo getPcOrder(Long orderId, String asset, String symbol,  Long startTime,  Long endTime);

    List<PcOrderVo> queryOrderList(Long userId, List<String> assetList, List<String> symbolList, Long gtOrderId, Long ltOrderId, Integer count, List<Integer> statusList, Long startTime, Long endTime);

    PageResult<UserOrderVo> pageQueryOrderList(Long userId, String asset, String symbol, Integer status, Integer closeFlag, Long orderId, Integer pageNo, Integer pageSize, Long startTime, Long endTime);

    PageResult<PcOrderVo> queryUserActivityOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber);


    PageResult<PcOrderVo> queryAll(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer pageSize, Integer isPageYes,Long startTime,Long endTime);

    PageResult<PcOrderVo> queryActivityOrder(Long userId, String asset, List<String> symbolList, Integer status, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber,Long startTime,Long endTime);

    BigDecimal queryTotalFee(Long startTime, Long endTime, String asset, String symbol);

    BigDecimal queryTotalOrder(Long startTime, Long endTime, String asset, String symbol);
}
