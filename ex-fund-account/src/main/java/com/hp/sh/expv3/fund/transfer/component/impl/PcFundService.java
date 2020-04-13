package com.hp.sh.expv3.fund.transfer.component.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.fund.transfer.component.FundService;
import com.hp.sh.expv3.fund.transfer.constant.AccountType;
import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;
import com.hp.sh.expv3.pc.api.PcAccountCoreApi;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.vo.request.PcAddRequest;
import com.hp.sh.expv3.pc.vo.request.PcCutRequest;

@Component
public class PcFundService implements FundService{

	private final int accountType = AccountType.PC;

	@Autowired
	private PcAccountCoreApi pcAccountCoreApi;
	
	public void addFund(FundTransfer record){
		PcAddRequest request = new PcAddRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo("T_" + record.getTargetAccountType() + "_"+record.getSn());
		request.setTradeType(PcAccountTradeType.FUND_TO_PC);
		request.setUserId(record.getUserId());
		request.setAssociatedId(record.getId());
		pcAccountCoreApi.add(request);
	}

	@Override
	public void cutFund(FundTransfer record) {
		PcCutRequest request = new PcCutRequest();
		request.setAmount(record.getAmount());
		request.setAsset(record.getAsset());
		request.setRemark(record.getRemark());
		request.setTradeNo("T_" + record.getTargetAccountType() + "_"+record.getSn());
		request.setTradeType(PcAccountTradeType.PC_TO_FUND);
		request.setUserId(record.getUserId());
		request.setAssociatedId(record.getId());
		pcAccountCoreApi.cut(request);
	}

	@Override
	public BigDecimal getBalance(Long userId, String asset) {
		return pcAccountCoreApi.getBalance(userId, asset);
	}

	public int getAccountType() {
		return accountType;
	}
	
}
