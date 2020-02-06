package com.hp.sh.expv3.bb.mq.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.constant.LogType;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.msg.BBAccountLog;

/**
 * 发送事件消息
 * 
 * @author wangjg
 * 
 */
@Component
public class LogEventListener {
	
	@Autowired
	private EventSender sender;
	
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
			return LogType.TYPE_ACCOUNT_FUND_TO_PC;
		}
		if(tradeType==BBAccountTradeType.BB_TO_FUND){
			return LogType.TYPE_ACCOUNT_PC_TO_FUND;
		}
		if(tradeType>=BBAccountTradeType.ADD_TO_MARGIN && tradeType<=BBAccountTradeType.LEVERAGE_ADD_MARGIN){
			return tradeType-1;
		}
		return null;
	}

	private void sendEventMsg(BBAccountLog logMsg) {
//		sender.sendEventMsg(logMsg);
	}

}
