package com.hp.sh.expv3.fund.api4v2;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.base.BaseApiAction;
import com.hp.sh.expv3.fund.cash.api.ChainCasehApiAction;

@RestController
@RequestMapping("/inner/api/msg/send1")
public class FundAccount4ChainServer extends BaseApiAction implements FundAccount4ChainServerDef{

	@Autowired
	private ChainCasehApiAction chainCasehApiAction;
	
	@Override
	public long createDepositOrder(String operator, long accountId, String asset, String chainServerOrderId,
			String txHash, BigDecimal volume, long depositTime, String address, String token) {
		chainCasehApiAction.createDeposit(accountId, chainServerOrderId, asset, address, volume, txHash);
		return 0;
	}

	@Override
	public boolean depositOrderConfirm(String operator, long accountId, String asset, long depositId, String token) {
//		chainCasehApiAction.depositSuccess(accountId, depositId);
		return true;
	}

	@Override
	public boolean depositOrderFail(String operator, long accountId, String asset, long depositId, String token) {
//		chainCasehApiAction.depositFail(accountId, depositId);
		return true;
	}

	@Override
	public boolean updateWithdrawOrder(String operator, long accountId, String asset, int status, String token) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
