package com.hp.sh.expv3.fund.c2c.service;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.fund.c2c.constants.C2cConst;
import com.hp.sh.expv3.fund.c2c.dao.C2cOrderDAO;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.cash.constant.ApprovalStatus;
import com.hp.sh.expv3.fund.cash.constant.PaymentStatus;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import groovy.util.logging.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     *
     * @param payStatus 支付状态:0-待支付，1-支付成功，2-支付失败,3:已取消, 4-同步余额, 5-审核中, 6-审核通过
     * @param nextPage  1:下一页，-1：上一页
     * @param pageSize  页大小
     * @param id        主键id
     * @return
     */
    public PageResult<C2cOrderVo> pageQueryByPayStatus(Integer payStatus, Integer nextPage, Integer pageSize, Long id, Long userId) {
        PageResult<C2cOrderVo> pageResult = new PageResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("payStatus", payStatus);
        map.put("userId", userId);
        Long count = c2cOrderDAO.queryCount(map);
        pageResult.setRowTotal(count);
        List<C2cOrderVo> orderList = c2cOrderDAO.pageQueryByPayStatus(payStatus, nextPage, pageSize, id, userId,null);
        pageResult.setList(orderList);
        Integer rowTotal = Integer.parseInt(String.valueOf(count));
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }

    @CrossDB
    public List<C2cOrderVo> queryByPayStatus(Integer payStatus) {
        List<C2cOrderVo> orderList = c2cOrderDAO.queryByPayStatus(payStatus);

        return orderList;
    }

    public PageResult<C2cOrderVo> pageQueryByApprovalStatus(Integer approvalStatus, Integer pageNo, Integer pageSize, Long userId) {
        PageResult<C2cOrderVo> pageResult = new PageResult<>();
        PageHelper.startPage(pageNo, pageSize);

        List<C2cOrderVo> orderList = c2cOrderDAO.pageQueryByApprovalStatus(approvalStatus, userId,C2cConst.C2C_SELL);
        PageInfo<C2cOrderVo> info = new PageInfo<>(orderList);
        pageResult.setList(orderList);
        pageResult.setRowTotal(info.getTotal());
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        return pageResult;
    }

    public BigDecimal getLockC2cNumber(Long userId, String asset) {
        // 获取到c2c 审核中的出金
        List<C2cOrder> c2cOrders = c2cOrderDAO.queryList(new HashMap<String, Object>() {
            {
                put("userId", userId);
                put("payCurrency", asset);
                put("type", C2cConst.C2C_SELL);
                put("approvalStatus", C2cConst.C2C_APPROVAL_STATUS_PASS);
                put("payStatus", C2cConst.C2C_PAY_STATUS_NO_PAYMENT);

            }
        });

        return c2cOrders.stream().map(C2cOrder::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public C2cOrder queryById(Long id) {
        return c2cOrderDAO.queryById(id);
    }
}
