/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.pc.component.impl;

import com.gitee.hupadev.commons.cache.Cache;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 标记价格服务
 *
 * @author wangjg
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MarkPriceServiceImpl2 implements MarkPriceService {
    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;

    @Override
    public BigDecimal getCurrentMarkPrice(String asset, String symbol) {
        String key="markPrice:pc:current:";
        String s = templateDB5.opsForValue().get(key+asset+":"+symbol);
        return new BigDecimal(s);
    }

}