package com.hp.sh.expv3.fund.c2c.service;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.fund.c2c.constants.C2cConst;
import com.hp.sh.expv3.fund.c2c.dao.C2cOrderDAO;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.constant.ApprovalStatus;
import com.hp.sh.expv3.fund.constant.PaymentStatus;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 查询服务
 *
 * @author wangjg
 */
@Service
@Slf4j
@Transactional
public class QueryService {

    @Autowired
    private C2cOrderDAO c2cOrderDAO;

    /**
     * 通过支付状态分页查询c2c订单，不传则查全部
     * 当前订单（0-待支付，4-审核中）
     * 已完成订单（1-支付成功,5-审批通过）
     * 已取消订单（3:超时已取消，6-审批拒绝）
     *
     * @param status   支付状态:0-待支付，1-支付成功，2-支付失败,3:已取消, 4-审批中, 5-审批通过, 6-审批拒绝
     * @param nextPage 1:下一页，-1：上一页
     * @param pageSize 页大小
     * @param id       主键id
     * @return
     */
    public PageResult<C2cOrderVo> pageQueryByPayStatus(String status, Integer nextPage, Integer pageSize, Long id, Long userId) {
        PageResult<C2cOrderVo> pageResult = new PageResult<>();
        Map<String, Object> map = new HashMap<>();
        String[] split = status.split(",");
        Integer payStatus = Integer.parseInt(split[0]);
        Integer approvalStatus = Integer.parseInt(split[1]);
        map.put("userId", userId);
        map.put("payStatus", payStatus);
        map.put("approvalStatus", approvalStatus);
        if (payStatus == C2cConst.C2C_PAY_STATUS_CANCELED) {
            List<C2cOrderVo> list = new ArrayList<>();
            //已取消订单=超时订单+审核拒绝订单
            //超时订单
            map.put("approvalStatus", null);
            Long timeOutCount = c2cOrderDAO.queryCount(map);
            map.put("id", id);
            map.put("nextPage", nextPage);
            List<C2cOrderVo> timeOutC2cOrderVos = c2cOrderDAO.queryByStatus(map);
//            List<C2cOrderVo> orderList = c2cOrderDAO.pageQueryByPayStatus(payStatus,null, nextPage, pageSize, id, userId, null);
            list.addAll(timeOutC2cOrderVos);

            //审核拒绝订单
            map.put("payStatus", null);
            map.put("approvalStatus", C2cConst.C2C_APPROVAL_STATUS_REJECTED);
            Long rejectedCount = c2cOrderDAO.queryCount(map);
            Long count = timeOutCount + rejectedCount;
            pageResult.setRowTotal(count);

            List<C2cOrderVo> rejectedC2cOrderVos = c2cOrderDAO.queryByStatus(map);
//            List<C2cOrderVo> orderList = c2cOrderDAO.pageQueryByPayStatus(payStatus,null, nextPage, pageSize, id, userId, null);
            list.addAll(rejectedC2cOrderVos);

            this.convertPayStatus(list);
            List<C2cOrderVo> voList = list.stream().sorted(Comparator.comparing(C2cOrderVo::getId, Comparator.reverseOrder())).collect(Collectors.toList());
            List<C2cOrderVo> c2cOrderVos = voList.stream().limit(pageSize).collect(Collectors.toList());
            pageResult.setList(c2cOrderVos);
            Integer rowTotal = Integer.parseInt(String.valueOf(count));
            pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
            return pageResult;
        } else if (payStatus == C2cConst.C2C_PAY_STATUS_NO_PAYMENT) {

        }

        Long count = c2cOrderDAO.queryCount(map);
        pageResult.setRowTotal(count);
        List<C2cOrderVo> orderList = c2cOrderDAO.pageQueryByPayStatus(payStatus, approvalStatus, nextPage, pageSize, id, userId, null);
        this.convertPayStatus(orderList);
        pageResult.setList(orderList);
        Integer rowTotal = Integer.parseInt(String.valueOf(count));
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }

    private void convertPayStatus(List<C2cOrderVo> orderList) {
        if (!CollectionUtils.isEmpty(orderList)) {
            for (C2cOrderVo cOrderVo : orderList) {
                //      当前订单0（0-待支付，4-审核中）
                //     已完成订单1（1-支付成功,5-审批通过）
                //     已取消订单3（3:超时已取消，6-审批拒绝）
                if (cOrderVo.getPayStatus() == C2cConst.C2C_PAY_STATUS_CANCELED) {
                    cOrderVo.setPayStatus(C2cConst.C2C_PAY_STATUS_CANCELED);
                } else if (cOrderVo.getPayStatus() == C2cConst.C2C_PAY_STATUS_PAY_SUCCESS && cOrderVo.getApprovalStatus() == C2cConst.C2C_APPROVAL_STATUS_IN_AUDIT) {
                    cOrderVo.setPayStatus(C2cConst.C2C_APPROVAL_STATUS_IN_AUDIT);
                } else if (cOrderVo.getPayStatus() == C2cConst.C2C_PAY_STATUS_PAY_SUCCESS && cOrderVo.getApprovalStatus() == C2cConst.C2C_APPROVAL_STATUS_PASS) {
                    cOrderVo.setPayStatus(C2cConst.C2C_PAY_STATUS_PAY_SUCCESS);
                } else if (cOrderVo.getApprovalStatus() == C2cConst.C2C_APPROVAL_STATUS_REJECTED) {
                    cOrderVo.setPayStatus(C2cConst.C2C_APPROVAL_STATUS_REJECTED);
                }
            }
        }
    }

    @CrossDB
    public List<C2cOrderVo> queryByPayStatus(Integer payStatus, String payStatusDesc) {
        List<C2cOrderVo> orderList = c2cOrderDAO.queryByPayStatus(payStatus, payStatusDesc);

        return orderList;
    }

    public PageResult<C2cOrderVo> pageQueryByApprovalStatus(Integer approvalStatus, Integer pageNo, Integer pageSize, Long userId) {
        PageResult<C2cOrderVo> pageResult = new PageResult<>();
        PageHelper.startPage(pageNo, pageSize);

        List<C2cOrderVo> orderList = c2cOrderDAO.pageQueryByApprovalStatus(approvalStatus, userId, C2cConst.C2C_SELL);
        PageInfo<C2cOrderVo> info = new PageInfo<>(orderList);
        pageResult.setList(orderList);
        pageResult.setRowTotal(info.getTotal());
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        return pageResult;
    }

    public BigDecimal getLockC2cNumber(Long userId, String asset) {
        // 获取到c2c 审核中的出金
//        List<C2cOrder> c2cOrders = c2cOrderDAO.queryList(new HashMap<String, Object>() {
//            {
//                put("userId", userId);
//                put("payCurrency", asset);
//                put("type", C2cConst.C2C_SELL);
//                put("approvalStatus", C2cConst.C2C_APPROVAL_STATUS_IN_AUDIT);
//                put("payStatus", C2cConst.C2C_PAY_STATUS_NO_PAYMENT);
//
//            }
//        });
//
//        return c2cOrders.stream().map(C2cOrder::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
        return  c2cOrderDAO.queryC2cFrozenAsset(userId,asset);
    }

    public C2cOrder queryById(Long id) {
        return c2cOrderDAO.queryById(id);
    }

    public BigDecimal queryTotalNumber(String asset, Integer type, Integer payStatus) {
        BigDecimal total = c2cOrderDAO.queryTotalNumber(asset, type, payStatus);
        if (null == total) {
            return BigDecimal.ZERO;
        }
        return total;
    }
}
