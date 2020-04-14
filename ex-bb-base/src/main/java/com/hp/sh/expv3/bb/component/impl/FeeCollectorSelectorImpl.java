package com.hp.sh.expv3.bb.component.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.FeeCollectorSelector;

/**
 * @author BaiLiJun  on 2019/12/18
 */
@Primary
@Component
public class FeeCollectorSelectorImpl implements FeeCollectorSelector {

    //获取手续费收取人账户(这里暂时写死，随机获取一个收取人账户,后期完善)
    @Override
    public Long getFeeCollectorId(Long userId, String asset, String symbol) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int index = (int) (Math.random() * arr.length);
        long aLong = arr[index];
        return aLong;
    }
}
