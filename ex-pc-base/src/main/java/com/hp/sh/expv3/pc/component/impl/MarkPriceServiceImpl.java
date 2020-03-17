/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.pc.component.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.hp.sh.expv3.config.redis.RedisConfig;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;

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

    //查询最新标记价格
    @Override
    public MarkPriceVo getLastMarkPrice(String asset, String symbol) {
        MarkPriceVo vo = new MarkPriceVo();
        String key = "markPrice:pc:history:";
        Set<String> set = templateDB5.boundZSetOps(key + asset + ":" + symbol).reverseRange(0, 0);
       if(!CollectionUtils.isEmpty(set)){
           ArrayList<String> list = new ArrayList<>(set);
           String s = list.get(0);
           String[] split = s.split("#");
           vo.setMarkPrice(new BigDecimal(split[0]));
           vo.setTime(Long.parseLong(split[1]));
       }
        return vo;
    }

    public BigDecimal getLatestPrice(String asset, String symbol){
        String key = "lastPrice:pc:";
        String s = templateDB5.opsForValue().get(key + asset + ":" + symbol);
        return new BigDecimal(s);
    }

}