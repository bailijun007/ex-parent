package com.hp.sh.expv3.pc.component.impl;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.component.FeeCollectorSelector;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderTradeDAO;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Primary
@Component
public class FeeCollectorSelectorImpl implements FeeCollectorSelector {
    @Autowired
    private PcOrderTradeDAO pcOrderTradeDAO;

    //获取手续费收取人账户(这里暂时写死，随机获取一个收取人账户,后期完善)
    @Override
    public Long getFeeCollectorId(Long userId, String asset, String symbol) {
        return new Random().nextLong();
    }
}
