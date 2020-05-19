package com.hp.sh.expv3.test;


import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.mapper.KlineDataMapper;
import com.hp.sh.expv3.pojo.BBSymbol;
import com.hp.sh.expv3.pojo.KlineDataPo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/5/7
 */
@SpringBootTest
@ActiveProfiles("dev")
@RunWith(SpringRunner.class)
public class TestDemo {
    @Autowired
    private KlineDataMapper klineDataMapper;

    @Resource(name = "metadataTemplateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "prodMetadataTemplateDB5")
    private StringRedisTemplate prodTemplateDB5;


    @Test
    public void test1(){
        String key="bb_symbol";
        HashOperations opsForHash = templateDB0.opsForHash();
        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(key, ScanOptions.NONE);

        List<BBSymbol> list = new ArrayList<>();
        while (curosr.hasNext()) {
            Map.Entry<String, Object> entry = curosr.next();
            Object o = entry.getValue();
            BBSymbol bBSymbolVO = JSON.parseObject(o.toString(), BBSymbol.class);
            list.add(bBSymbolVO);
        }
        for (BBSymbol bbSymbol : list) {
            System.out.println(bbSymbol);
        }

    }

    @Test
    public void test3(){
         ValueOperations<String, String> opsForValue = prodTemplateDB5.opsForValue();
         String value = opsForValue.get("lastPrice:bb:USDT:BCH_USDT");
        System.out.println("value = " + value);
    }


    @Test
    public void test2(){
        String table="kline_data_202005";
        Integer klineType=1;
        String pair="BTC_USDT";
        String interval="1min";
        Long openTimeBegin=1588833240000L;
        Long openTimeEnd=1588844040000L;
        String expName="binance";
         List<KlineDataPo> klineDataPos = klineDataMapper.queryKlineDataByThirdData(table, klineType, pair, interval, openTimeBegin, openTimeEnd, expName);
        for (KlineDataPo klineDataPo : klineDataPos) {
            System.out.println("klineDataPo = " + klineDataPo);
        }

    }

}
