package com.hp.sh.expv3.fund.c2c.service;

import com.hp.sh.expv3.fund.c2c.dao.C2cOrderDAO;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 买入服务
 *
 * @author wangjg
 */
@Service
@Transactional
public class BuyService {
    private static final Logger logger = LoggerFactory.getLogger(BuyService.class);
    @Autowired
    private C2cOrderDAO c2cOrderDAO;


    public void saveC2cOrder(C2cOrder c2cOrder) {
        c2cOrderDAO.save(c2cOrder);
    }

    //通过sn修改订单
    public void updateBySnAndUserId(C2cOrder c2cOrder) {
//        c2cOrderDAO.update(c2cOrder);
        c2cOrderDAO.updateBySnAndUserId(c2cOrder);
    }

    //通过sn和userId查询订单
    public C2cOrder queryBySnAndUserId(String sn, Long userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("sn", sn);
        map.put("userId", userId);
        C2cOrder c2cOrder = c2cOrderDAO.queryOne(map);
        return c2cOrder;
    }

}
