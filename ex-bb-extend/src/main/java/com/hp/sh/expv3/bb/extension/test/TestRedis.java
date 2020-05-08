package com.hp.sh.expv3.bb.extension.test;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.dao.BbAccountExtMapper;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.thirdKlineData.mapper.ThirdKlineDataMapper;
import com.hp.sh.expv3.bb.extension.vo.BbAccountExtVo;
import com.hp.sh.expv3.bb.extension.vo.KlineDataPo;
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
public class TestRedis {
    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Test
    public void test1() {
        String key = "bb_symbol";
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
            String asset = "USDT";
            if (bbSymbol.getAsset().equals(asset)) {
                System.out.println(bbSymbol.getSymbol());
            }
        }
    }

@Autowired
    private BbAccountExtMapper bbAccountExtMapper;

    @Test
    public void test2() {
        Long userId=109027203634737280L;
         BbAccountExtVo bbAccountExtVo = bbAccountExtMapper.getBBAccount(userId, "ETH");
        System.out.println("bbAccountExtVo="+bbAccountExtVo.getAvailable());
    }

    @Autowired
    private ThirdKlineDataMapper klineDataMapper;

    @Test
    public void test3(){
        Long userId=109027203634737280L;
        BbAccountExtVo bbAccountExtVo = bbAccountExtMapper.getBBAccount(userId, "ETH");
        System.out.println("bbAccountExtVo="+bbAccountExtVo.getAvailable());

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
