package com.hp.sh.expv3.bb.kline.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;
import com.hp.sh.expv3.bb.kline.service.impl.BbKlineHistoryCalcByTradeFromExpServiceImpl;
import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiLiJun  on 2020/3/16
 */
@SpringBootTest
@ActiveProfiles("bai")
@RunWith(SpringRunner.class)
public class TestKline {

    @Autowired
    private BbKlineHistoryCalcByTradeFromExpServiceImpl bbKlineHistoryCalcByTradeFromExpService;

//    @Test
//    public void test(){
//        String asset="USDT";
//        String symbol="BTC_USDT";
//        long ms=1584671231527L;
//        long maxMs=1584674891000L;
//        final List<BbTradeVo> list = bbKlineHistoryCalcByTradeFromExpService.listTrade(asset, symbol, ms, maxMs);
//
//        System.out.println(list.size());
//
//    }

}
