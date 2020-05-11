package com.hp.sh.expv3.bb.extension.test;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.dao.BbAccountExtMapper;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.thirdKlineData.mapper.ThirdKlineDataMapper;
import com.hp.sh.expv3.bb.extension.vo.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/26
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestOrderTrade {
    @Autowired
    private BbAccountExtMapper bbAccountExtMapper;


    @Autowired
    private ThirdKlineDataMapper klineDataMapper;

    @Autowired
    private BbOrderTradeExtService bbOrderTradeExtService;

    @Test
    public void selectTradeListByUserId() {
        List<BbUserOrderTrade> tradeVo = bbOrderTradeExtService.selectTradeListByUserId("USDT", "BTC_USDT", 1588291200000L, 1589180264403L, 1L, null);
        System.out.println("tradeVo = " + tradeVo);

    }

    @Test
    public void selectBbFeeCollectByAccountId() {
        final List<BbOrderTradeDetailVo> detailVos = bbOrderTradeExtService.selectPcFeeCollectByAccountId("USDT", "BTC_USDT", 1L, 1588291200000L, 1589180264403L);
        System.out.println("detailVos = " + detailVos.toString());


    }

}
