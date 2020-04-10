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
		if(record.getSrcAccountType()==AccountType.FUND){
			request.setTradeType(BBAccountTradeType.ACCOUNT_FUND_TO_BB);
		}else if(record.getSrcAccountType()==AccountType.PC){
			request.setTradeType(BBAccountTradeType.ACCOUNT_PC_TO_BB);
		}else{
			throw new RuntimeException();
		}
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
		
		if(record.getTargetAccountType()==AccountType.FUND){
			request.setTradeType(BBAccountTradeType.ACCOUNT_BB_TO_FUND);
		}else if(record.getTargetAccountType()==AccountType.PC){
			request.setTradeType(BBAccountTradeType.ACCOUNT_BB_TO_PC);
		}else{
			throw new RuntimeException();
		}
		request.setUserId(record.getUserId());
		request.setAssociatedId(record.getId());
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
