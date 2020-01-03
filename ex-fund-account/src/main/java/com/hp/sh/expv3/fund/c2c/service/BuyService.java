package com.hp.sh.expv3.fund.c2c.service;

import com.hp.sh.expv3.fund.c2c.dao.C2cOrderDAO;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        System.out.println("c2cOrder = " + c2cOrder);
        int i = c2cOrderDAO.saveC2cOrder(c2cOrder);

        System.out.println("i = " + i);

    }

    //通过sn修改订单
    public void update(C2cOrder c2cOrder) {
//        c2cOrderDAO.update(c2cOrder);
    }
}