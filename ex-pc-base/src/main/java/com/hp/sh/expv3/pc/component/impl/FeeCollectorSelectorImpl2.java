package com.hp.sh.expv3.pc.component.impl;

import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FeeCollectorSelectorImpl2 implements FeeCollectorSelector {
    @Autowired
    private PcOrderTradeDAO pcOrderTradeDAO;

    //获取手续费收取人账户(这里暂时写死，随机获取一个收取人账户,后期完善)
    @Override
    public Long getFeeCollectorId(Long userId, String asset, String symbol) {
        return new Random().nextLong();
    }
}
