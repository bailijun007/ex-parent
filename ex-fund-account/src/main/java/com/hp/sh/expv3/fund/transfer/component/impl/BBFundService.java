package com.hp.sh.expv3.fund.transfer.component.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.api.BBAccountCoreApi;
import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.vo.request.BBAddRequest;
import com.hp.sh.expv3.bb.vo.request.BBCutRequest;
import com.hp.sh.expv3.fund.transfer.component.FundService;
import com.hp.sh.expv3.fund.transfer.constant.AccountType;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;

@Component
public class BBFundService implements FundService{

	private final int accountType = AccountType.BB;

	@Autowired
	private BBAccountCoreApi bbAccountCoreApi;
	
	public void addFund(FundTransfer record){
		BBAddRequest request = new BBAddRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(BBAccountTradeType.FUND_TO_BB);
		request.setUserId(record.getUserId());
		request.setAssociatedId(record.getId());
		bbAccountCoreApi.add(request);
	}

	@Override
	public void cutFund(FundTransfer record) {
		BBCutRequest request = new BBCutRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo(record.getSn());
		request.setTradeType(BBAccountTradeType.BB_TO_FUND);
		request.setUserId(record.getUserId());
		bbAccountCoreApi.cut(request);
	}

	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return bbAccountCoreApi.getBalance(userId, asset);
	}
	
	public int getAccountType() {
		return accountType;
	}
	
}
