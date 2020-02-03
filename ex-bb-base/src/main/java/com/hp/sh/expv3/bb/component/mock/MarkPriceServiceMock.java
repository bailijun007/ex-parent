/**
 * @author 10086
 * @date 2019/11/5
 */
package com.hp.sh.expv3.bb.component.mock;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.MarkPriceService;
import com.hp.sh.expv3.bb.vo.response.MarkPriceVo;

/**
 * 标记价格服务
 * @author wangjg
 *
 */
@Component
public class MarkPriceServiceMock implements MarkPriceService{

    public BigDecimal getCurrentMarkPrice(String asset, String symbol) {
        return BigDecimal.ONE;
    }

	@Override
	public MarkPriceVo getLastMarkPrice(String asset, String symbol) {
		MarkPriceVo mp = new MarkPriceVo();
		mp.setMarkPrice(BigDecimal.ONE);
		mp.setTime(System.currentTimeMillis());
		return mp;
	}

}