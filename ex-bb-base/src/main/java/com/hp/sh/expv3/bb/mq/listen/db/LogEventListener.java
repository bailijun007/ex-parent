package com.hp.sh.expv3.bb.mq.listen.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.constant.LogType;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.log.entity.BBAccountLog;
import com.hp.sh.expv3.bb.module.log.service.BBAccountLogService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 发送事件消息
 * 
 * @author wangjg
 * 
 */
@Component
public class LogEventListener {
	
	@Autowired
	private BBAccountLogService accountLogService;
	
	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void afterCommit(BBAccountRecord bBAccountRecord) {
		Integer type = this.getlogType(bBAccountRecord);
		if(type==null){
			return;
		}
		BBAccountLog logMsg = new BBAccountLog();
		logMsg.setUserId(bBAccountRecord.getUserId());
		logMsg.setAsset(bBAccountRecord.getAsset());
		logMsg.setSymbol(null);
		logMsg.setTime(bBAccountRecord.getCreated());
		logMsg.setType(type);
		logMsg.setRefId(bBAccountRecord.getId());
		
		this.sendEventMsg(logMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void afterCommit(BBOrderTrade orderTrade) {
		BBAccountLog logMsg = new BBAccountLog();
		logMsg.setUserId(orderTrade.getUserId());
		logMsg.setAsset(orderTrade.getAsset());
		logMsg.setSymbol(orderTrade.getSymbol());
		logMsg.setTime(orderTrade.getCreated());
		logMsg.setType(this.getLogType(orderTrade));
		logMsg.setRefId(orderTrade.getId());
		
		this.sendEventMsg(logMsg);
	}

	private Integer getLogType(BBOrderTrade orderTrade) {
		if(IntBool.isTrue(orderTrade.getBidFlag())){
			return LogType.TRADE_BUY_IN;
		}else{
			return LogType.TRADE_SELL_OUT;
		}
	}

	private Integer getlogType(BBAccountRecord bBAccountRecord) {
		int tradeType = bBAccountRecord.getTradeType();
		if(tradeType==BBAccountTradeType.ACCOUNT_FUND_TO_BB){return LogType.ACCOUNT_FUND_TO_BB;}
		if(tradeType==BBAccountTradeType.ACCOUNT_BB_TO_FUND){return LogType.ACCOUNT_BB_TO_FUND;}
		if(tradeType==BBAccountTradeType.ACCOUNT_FUND_TO_PC){return LogType.ACCOUNT_FUND_TO_PC;}
		if(tradeType==BBAccountTradeType.ACCOUNT_PC_TO_FUND){return LogType.ACCOUNT_PC_TO_FUND;}
		return null;
	}

	@Transactional(rollbackFor=Exception.class)
	public void sendEventMsg(BBAccountLog logMsg) {
		this.accountLogService.save(logMsg);
	}

}
