/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.bb.component;

import java.math.BigDecimal;

import com.hp.sh.expv3.bb.vo.response.MarkPriceVo;

/**
 * 标记价格服务
 * @author wangjg
 *
 */
public interface MarkPriceService{

    public BigDecimal getCurrentMarkPrice(String asset, String symbol);

    public MarkPriceVo getLastMarkPrice(String asset, String symbol);
    
    /**
     * 获取最新成交价
     * @param asset
     * @param symbol
     * @return
     */
    default public BigDecimal getLatestPrice(String asset, String symbol){
    	return getCurrentMarkPrice(asset, symbol);
    }

}