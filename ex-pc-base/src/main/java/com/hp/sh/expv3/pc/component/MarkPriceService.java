/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.pc.component;

import java.math.BigDecimal;

/**
 * 标记价格服务
 * @author wangjg
 *
 */
public interface MarkPriceService{

    public BigDecimal getCurrentMarkPrice(String asset, String symbol);

}