package com.hp.sh.expv3.bb.mq.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.constant.LogType;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.collector.entity.BBAccountLog;
import com.hp.sh.expv3.bb.module.collector.service.BBAccountLogService;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;

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
		Integer type = this.getType(bBAccountRecord.getTradeType());
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
		logMsg.setType(orderTrade.getLogType());
		logMsg.setRefId(orderTrade.getId());
		
		this.sendEventMsg(logMsg);
	}

	private Integer getType(int tradeType) {
		if(tradeType==BBAccountTradeType.FUND_TO_BB){
			return LogType.TYPE_ACCOUNT_FUND_TO_BB;
		}
		if(tradeType==BBAccountTradeType.BB_TO_FUND){
			return LogType.TYPE_ACCOUNT_BB_TO_FUND;
		}
		return null;
	}

	@Transactional(rollbackFor=Exception.class)
	public void sendEventMsg(BBAccountLog logMsg) {
		this.accountLogService.save(logMsg);
	}

}
