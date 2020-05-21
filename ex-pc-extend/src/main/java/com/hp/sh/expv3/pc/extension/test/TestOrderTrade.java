package com.hp.sh.expv3.pc.extension.test;

import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/5/21
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOrderTrade {

    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;

    @Test
    public void queryOrderTrade() {
        List<PcOrderTradeVo> orderTradeVoList = pcOrderTradeService.queryOrderTrade(1L, "BTC", "BTC_USDT",  "1", 1589904000000L, 1589990400000L);
        System.out.println("orderTradeVoList = " + orderTradeVoList);
    }

}
