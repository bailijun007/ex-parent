/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.cache.Cache;

@Component
public class MarkPriceService{

    @Autowired
    private Cache cache;

    public BigDecimal getCurrentMarkPrice(String asset, String symbol) {
        return null;
    }

}