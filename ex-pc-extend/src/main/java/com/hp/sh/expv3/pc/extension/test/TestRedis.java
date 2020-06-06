package com.hp.sh.expv3.pc.extension.test;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.pc.constant.RedisKey;
import com.hp.sh.expv3.pc.extension.vo.PcContractVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.persistence.Table;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/6/1
 */
@ActiveProfiles("bai")
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedis {
    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Test
    public void test() {
        String asset = "EOS";
        String symbol = "EOS_USDT";
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset + "__" + symbol;

        Object o = hashOperations.get(RedisKey.PC_CONTRACT, hashKey);
        String json = o.toString();
        PcContractVO vo = JSON.parseObject(json, PcContractVO.class);
        System.out.println("vo = " + vo);

    }

    @Test
    public void test2() {
        Cursor<Map.Entry<Object, Object>> curosr = templateDB0.opsForHash().scan(RedisKey.PC_CONTRACT, ScanOptions.NONE);
        List<PcContractVO> list = new ArrayList<>();
        while (curosr.hasNext()) {
            Map.Entry<Object, Object> entry = curosr.next();
            Object o = entry.getValue();
            PcContractVO pcContractVO = JSON.parseObject(o.toString(), PcContractVO.class);
            list.add(pcContractVO);
        }
        for (PcContractVO pcContractVO : list) {
            System.out.println(pcContractVO);
        }
    }

}
