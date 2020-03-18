package com.hp.sh.expv3.fund.transfer.component;

import java.math.BigDecimal;

import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;

public interface FundService {
	
	public int getAccountType();
	
	public BigDecimal getBalance(Long userId, String asset);

	public void addFund(FundTransfer record);
	
	public void cutFund(FundTransfer record);
	
}
