/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.pc.component.mock;

import java.math.BigDecimal;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.component.MarkPriceService;

/**
 * 标记价格服务
 * @author wangjg
 *
 */
@Primary
@Component
public class MarkPriceServiceImpl implements MarkPriceService{

    public BigDecimal getCurrentMarkPrice(String asset, String symbol) {
        return BigDecimal.ONE;
    }

}