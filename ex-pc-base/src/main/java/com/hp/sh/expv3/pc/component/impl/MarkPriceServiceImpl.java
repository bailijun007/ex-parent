/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.pc.component.impl;

import java.math.BigDecimal;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.component.MarkPriceService;

/**
 * 标记价格服务
 *
 * @author wangjg
 */
@Primary
@Component
public class MarkPriceServiceImpl implements MarkPriceService {
    private static final Logger logger = LoggerFactory.getLogger(MarkPriceServiceImpl.class);
    
    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;

    @Override
    public BigDecimal getCurrentMarkPrice(String asset, String symbol) {
        String key = "markPrice:pc:current:";
        String s = templateDB5.opsForValue().get(key + asset + ":" + symbol);
        if(s==null){
        	logger.error("标记价格不存在!{}__{}",asset, symbol);
        	return null;
        }
        return new BigDecimal(s);
    }

}