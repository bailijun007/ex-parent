package com.hp.sh.expv3.fund.c2c.service;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.dao.C2cOrderDAO;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
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

    //通过sn和用户id修改订单
    public void updateBySnAndUserId(C2cOrder c2cOrder) {
        int count = c2cOrderDAO.updateBySnAndUserId(c2cOrder);
        if(count!=1){
            throw new ExException(ExFundError.UPDATE_C2C_ORDER_FAIL);
        }
    }

    //通过sn和userId查询订单
    public C2cOrder queryBySn(String sn) {
        C2cOrder c2cOrder = c2cOrderDAO.queryBySn(sn);
        return c2cOrder;
    }

}
