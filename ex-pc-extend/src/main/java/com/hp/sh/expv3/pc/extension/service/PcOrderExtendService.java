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

    List<PcOrderVo> orderList(Long closePosId, Long userId);

    List<PcOrderVo> findCurrentUserOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag);

    PageResult<PcOrderVo> queryAllOrders(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber);

    PageResult<PcOrderVo> queryHistoryOrders(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber);

    PcOrderVo getPcOrder(Long orderId, String asset, String symbol, Long userId);

    List<PcOrderVo> queryOrderList(Long userId, List<String> assetList, List<String> symbolList, Long gtOrderId, Long ltOrderId, Integer count, List<Integer> statusList);

    PageResult<UserOrderVo> pageQueryOrderList(Long userId, String asset, String symbol, Integer status, Integer closeFlag, Long orderId, Integer pageNo, Integer pageSize);

    PageResult<PcOrderVo> queryUserActivityOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage, Integer isTotalNumber);

}
