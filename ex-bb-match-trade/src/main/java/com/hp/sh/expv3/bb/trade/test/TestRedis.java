package com.hp.sh.expv3.bb.trade.test;

import com.hp.sh.expv3.config.redis.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author BaiLiJun  on 2020/4/21
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    @Qualifier("metadataRedisUtil")
    private RedisUtil metadataRedisUtil;

    @Test
    public void test4() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate localDate = LocalDate.now();
        try {
            final String format = localDate.format(dtf);

            int size = 2 / 0;
            String key = "bb:matchCount:" + "USDT" + ":" + "BTC_USDT" + ":" + format;
            System.out.println("key = " + key);
            metadataRedisUtil.incrBy(key, size);
        } catch (Exception e) {
e.printStackTrace();
        }

    }
}
