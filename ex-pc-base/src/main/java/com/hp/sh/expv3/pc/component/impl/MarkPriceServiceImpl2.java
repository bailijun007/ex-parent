/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.pc.component.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Set;

import javax.annotation.Resource;

import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.component.MarkPriceService;
import org.springframework.util.CollectionUtils;

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
        String key = "markPrice:pc:current:";
        String s = templateDB5.opsForValue().get(key + asset + ":" + symbol);
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

}