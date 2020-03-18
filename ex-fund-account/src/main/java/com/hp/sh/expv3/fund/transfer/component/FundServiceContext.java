package com.hp.sh.expv3.fund.transfer.component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;

@Component
public class FundServiceContext {

	private final Map<Integer, FundService> map = new HashMap<Integer, FundService>();
	
	public BigDecimal getBalance(Long userId, int accountType, String asset){
		FundService fs = this.map.get(accountType);
		BigDecimal balance = fs.getBalance(userId, asset);
		if(balance==null){
			return BigDecimal.ZERO;
		}
		return balance;
	}
	
	public void cutSrcFund(FundTransfer record){
		FundService fs = this.map.get(record.getSrcAccountType());
		fs.cutFund(record);
	}
	
	public void addTargetFund(FundTransfer record){
		FundService fs = this.map.get(record.getTargetAccountType());
		fs.addFund(record);
	}
	
	@Autowired
	void setFundServiceList(List<FundService> fsList){
		for(FundService fs:fsList){
			this.map.put(fs.getAccountType(), fs);
		}
	}
}
