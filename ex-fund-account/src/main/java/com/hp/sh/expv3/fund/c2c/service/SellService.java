package com.hp.sh.expv3.fund.c2c.service;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.c2c.dao.C2cOrderDAO;
import com.hp.sh.expv3.fund.c2c.entity.C2cOrder;
import com.hp.sh.expv3.fund.extension.error.ExFundError;
import groovy.util.logging.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 卖出服务
 *
 * @author wangjg
 */
@Service
@Slf4j
@Transactional
public class SellService {

    @Autowired
    private C2cOrderDAO c2cOrderDAO;

    public void createC2cOut(C2cOrder c2cOrder) {
        c2cOrderDAO.save(c2cOrder);
    }

    public void updateById(C2cOrder c2cOrder){
        int count = c2cOrderDAO.updateById(c2cOrder);
        if(count!=1){
            throw new ExException(ExFundError.UPDATE_C2C_ORDER_FAIL);
        }
    }


}
