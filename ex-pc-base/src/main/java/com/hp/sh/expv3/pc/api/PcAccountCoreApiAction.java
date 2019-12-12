/**
 * 
 */
package com.hp.sh.expv3.pc.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.api.PcAccountCoreApi;
import com.hp.sh.expv3.pc.api.request.AddMoneyRequest;
import com.hp.sh.expv3.pc.api.request.CutMoneyRequest;
import com.hp.sh.expv3.pc.module.account.service.impl.PcAccountCoreService;

/**
 * 永续合约账户核心接口
 * @author wangjg
 */
@RestController
public class PcAccountCoreApiAction implements PcAccountCoreApi {

	@Autowired
	private PcAccountCoreService pcAccountCoreService;
	
	@Override
	public Integer add(AddMoneyRequest request) {
		return pcAccountCoreService.add(request);
	}

	@Override
	public Integer cut(CutMoneyRequest request) {
		return pcAccountCoreService.cut(request);
	}

	@Override
	public Boolean checkTradNo(Long userId, String tradeNo) {
		return pcAccountCoreService.checkTradNo(userId, tradeNo);
	}

	@Override
	public int createAccount(Long userId, String asset) {
		return pcAccountCoreService.createAccount(userId, asset);
	}

	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return pcAccountCoreService.getBalance(userId, asset);
	}
	
}