package com.hp.sh.expv3.pc.extension.test;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.api.PcOrderExtendApiAction;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.util.CommonDateUtils;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/20
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOrder {
    @Autowired
    private PcOrderExtendApiAction pcOrderExtendApiAction;


    @Test
    public void queryOrderList() {
        List<UserOrderVo> list = pcOrderExtendApiAction.queryOrderList(1L, "BTC", "BTC_USDT", null, null, 50, "1", null, null);
        System.out.println("list = " + list);
    }

    @Test
    public void queryUserActivityOrder() {
        PageResult<UserOrderVo> result = pcOrderExtendApiAction.queryUserActivityOrder(1L, "BTC", null, null, null, null, null, 1, 20, null, 1, null, null);
        System.out.println("result = " + result);
    }

    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;

    @Test
    public void queryOrderTrade() {
        List<PcOrderTradeVo> orderTradeVoList = pcOrderTradeService.queryOrderTrade(1L, "BTC", "BTC_USDT",  "1", 1589904000000L, 1589990400000L);
        System.out.println("orderTradeVoList = " + orderTradeVoList);
    }

}
