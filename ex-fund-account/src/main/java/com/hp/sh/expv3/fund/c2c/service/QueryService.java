package com.hp.sh.expv3.fund.c2c.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.c2c.dao.C2cOrderDAO;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.extension.vo.C2cOrderVo;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public PageResult<C2cOrderVo> pageQueryByPayStatus(Integer payStatus, Integer nextPage, Integer pageSize, Integer id) {
        PageResult<C2cOrderVo> pageResult = new PageResult<>();
        List<C2cOrderVo> orderList = c2cOrderDAO.pageQueryByPayStatus(payStatus, nextPage, pageSize, id);
        Map<String, Object> map = new HashMap<>();
        map.put("payStatus", payStatus);
        Long count = c2cOrderDAO.queryCount(map);
        pageResult.setList(orderList);
        pageResult.setRowTotal(count);
        Integer rowTotal = Integer.parseInt(String.valueOf(count));
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }


}
