package com.hp.sh.expv3.fund.transfer.component.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.fund.transfer.component.FundService;
import com.hp.sh.expv3.fund.transfer.constant.AccountType;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;
import com.hp.sh.expv3.fund.wallet.api.FundAccountCoreApi;
import com.hp.sh.expv3.fund.wallet.constant.TradeType;
import com.hp.sh.expv3.fund.wallet.vo.request.FundAddRequest;
import com.hp.sh.expv3.fund.wallet.vo.request.FundCutRequest;

@Component
public class FundFundService implements FundService{
	
	private final int accountType = AccountType.FUND;

	@Autowired
	private FundAccountCoreApi fundAccountCoreApi;
	
	public void addFund(FundTransfer record){
		FundAddRequest request = new FundAddRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(TradeType.TRANSFER_IN);
		request.setUserId(record.getUserId());
		request.setAssociatedId(record.getId());
		fundAccountCoreApi.add(request);
	}

	@Override
	public void cutFund(FundTransfer record) {
		FundCutRequest request = new FundCutRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(TradeType.TRANSFER_OUT);
		request.setUserId(record.getUserId());
		request.setAssociatedId(record.getId());
		fundAccountCoreApi.cut(request);
	}

	public int getAccountType() {
		return accountType;
	}

	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return fundAccountCoreApi.getBalance(userId, asset);
	}
	
}
