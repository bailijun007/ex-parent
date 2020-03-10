package com.hp.sh.expv3.bb.extension.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.bb.extension.service.BBTradeFeeExtService;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@RestController
public class BBTradeFeeExtApiAction implements BBTradeFeeExtApi {
   @Autowired
    private BBTradeFeeExtService tradeFeeService;

	@Override
	public BigDecimal query(Long userId, String asset, Integer makerFlag, Long beginTime, Long endTime) {
		BigDecimal total = tradeFeeService.query(userId, asset, makerFlag, beginTime, endTime);
		return total!=null?total:BigDecimal.ZERO;
	}

}
