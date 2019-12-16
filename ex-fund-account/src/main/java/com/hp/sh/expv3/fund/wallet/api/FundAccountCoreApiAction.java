/**
 * 
 */
package com.hp.sh.expv3.fund.wallet.api;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.fund.wallet.service.FundAccountCoreService;
import com.hp.sh.expv3.fund.wallet.vo.request.AddMoneyRequest;
import com.hp.sh.expv3.fund.wallet.vo.request.CutMoneyRequest;

/**
 * 账户核心服务
 * @author wangjg
 */
@RestController
public class FundAccountCoreApiAction implements FundAccountCoreApi{
	
	@Autowired
	private FundAccountCoreService fundAccountCoreService;

	@Override
	public void add(AddMoneyRequest request) {
		fundAccountCoreService.add(request);
	}

	@Override
	public void cut(CutMoneyRequest request) {
		fundAccountCoreService.cut(request);
	}

	@Override
	public Boolean checkTradNo(Long userId, String tradeNo) {
		return fundAccountCoreService.checkTradNo(userId, tradeNo);
	}

	@Override
	public int createAccount(Long userId, String asset) {
		return fundAccountCoreService.createAccount(userId, asset);
	}

	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return fundAccountCoreService.getBalance(userId, asset);
	}
	
	
}