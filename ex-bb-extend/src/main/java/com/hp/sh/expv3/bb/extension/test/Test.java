package com.hp.sh.expv3.bb.extension.test;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.constant.BbKLineKey;
import com.hp.sh.expv3.bb.extension.pojo.BBKLine;
import com.hp.sh.expv3.bb.extension.pubsub.BBKlineBuild;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiLiJun  on 2020/3/5
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Test {

    @Resource(name = "klineTemplateDB5")
    private StringRedisTemplate klineTemplateDB5;

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;



    @org.junit.Test
    public void test1() {
        final Set<Object> xbbb_symbol = klineTemplateDB5.opsForHash().keys("bb_symbol");
        System.out.println(xbbb_symbol.size());
        final Set<Object> bb_symbol = templateDB0.opsForHash().keys("bb_symbol");
        System.out.println(bb_symbol.size());
    }

    @org.junit.Test
    public void test2() {
        BBKLine bbkLine = new BBKLine();
        bbkLine.setAsset("BTC");
        bbkLine.setSymbol("BTC_USDT");
        bbkLine.setSequence(1);
        bbkLine.setMinute(26388150L);
        bbkLine.setHigh(new BigDecimal(8727.580000000000000000000000000000 + ""));
        bbkLine.setLow(new BigDecimal(8727.580000000000000000000000000000 + ""));
        bbkLine.setOpen(new BigDecimal(8727.580000000000000000000000000000 + ""));
        bbkLine.setClose(new BigDecimal(8727.580000000000000000000000000000 + ""));
        bbkLine.setVolume(new BigDecimal(0.019791000000000000000000000000 + ""));
        //向集合中插入元素，并设置分数
        templateDB0.opsForZSet().add(BbKLineKey.BB_KLINE_TASK + "BTC" + ":" + "BTC_USDT", JSON.toJSONString(bbkLine), 26388150);
        Set<String> range = templateDB0.opsForZSet().range("testkline:from_exp:repair:BB:" + "BTC" + ":" + "BTC_USDT:1", 0, -1);
        System.out.println(range);
    }
@Autowired
    BBKlineBuild bbKlineBuild;

    @org.junit.Test
    public void testoubsub(){
        bbKlineBuild.trigger();
    }

    @org.junit.Test
    public void test(){
        final long l = TimeUnit.MILLISECONDS.toMinutes(1583394926831L);
        System.out.println("l = " + l);
    }

    @org.junit.Test
    public void test3(){
        String asset="BTC";
        String symbol="BTC_USDT";
        Set<ZSetOperations.TypedTuple<String>> task = templateDB0.opsForZSet().rangeWithScores(BbKLineKey.BB_KLINE_TASK + asset + ":" + symbol, 0, -1);
        if (CollectionUtils.isEmpty(task)) {
            System.out.println("task = " + task);
        }
        for (ZSetOperations.TypedTuple<String> tuple : task) {
            final BBKLine bbkLine = JSON.parseObject(tuple.getValue(), BBKLine.class);
            System.out.println("k线1分钟时间为："+bbkLine.getMinute());
        }
    }

}
