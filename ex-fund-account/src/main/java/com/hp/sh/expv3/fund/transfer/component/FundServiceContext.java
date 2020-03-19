package com.hp.sh.expv3.fund.transfer.component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.fund.transfer.entity.FundTransfer;

@Component
public class FundServiceContext {
	private static final Logger logger = LoggerFactory.getLogger(FundServiceContext.class);

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
		if(fs==null){
			logger.error("FundService 不存在 {}", record);
		}
		fs.cutFund(record);
	}
	
	public void addTargetFund(FundTransfer record){
		FundService fs = this.map.get(record.getTargetAccountType());
		if(fs==null){
			logger.error("FundService 不存在 {}", record);
		}
		fs.addFund(record);
	}
	
	@Autowired
	void setFundServiceList(List<FundService> fsList){
		for(FundService fs:fsList){
			this.map.put(fs.getAccountType(), fs);
		}
	}
}
