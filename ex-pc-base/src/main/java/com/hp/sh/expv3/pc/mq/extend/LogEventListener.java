package com.hp.sh.expv3.pc.mq.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecord;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.position.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.msg.LogType;
import com.hp.sh.expv3.pc.msg.PcAccountLog;
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
	private EventSender sender;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcAccountRecord pcAccountRecord) {
		Integer type = this.getType(pcAccountRecord.getTradeType());
		if(type==null){
			return;
		}
		PcAccountLog logMsg = new PcAccountLog();
		logMsg.setUserId(pcAccountRecord.getUserId());
		logMsg.setAsset(pcAccountRecord.getAsset());
		logMsg.setSymbol(null);
		logMsg.setTime(pcAccountRecord.getCreated().getTime());
		logMsg.setType(type);
		logMsg.setRefId(pcAccountRecord.getId());
		
		this.sendEventMsg(logMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcOrderTrade orderTrade) {
		PcAccountLog logMsg = new PcAccountLog();
		logMsg.setUserId(orderTrade.getUserId());
		logMsg.setAsset(orderTrade.getAsset());
		logMsg.setSymbol(orderTrade.getSymbol());
		logMsg.setTime(orderTrade.getCreated().getTime());
		logMsg.setType(orderTrade.getTradType());
		logMsg.setRefId(orderTrade.getId());
		
		this.sendEventMsg(logMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcLiqRecord liqRecord) {
		PcAccountLog logMsg = new PcAccountLog();
		logMsg.setUserId(liqRecord.getUserId());
		logMsg.setAsset(liqRecord.getAsset());
		logMsg.setSymbol(liqRecord.getSymbol());
		logMsg.setTime(liqRecord.getCreated().getTime());
		logMsg.setType(IntBool.isTrue(liqRecord.getLongFlag())?LogType.TYPE_LIQ_LONG:LogType.TYPE_LIQ_SHORT);
		logMsg.setRefId(liqRecord.getId());
		
		this.sendEventMsg(logMsg);
	}

	private Integer getType(int tradeType) {
		if(tradeType==PcAccountTradeType.FUND_TO_PC){
			return LogType.TYPE_ACCOUNT_FUND_TO_PC;
		}
		if(tradeType==PcAccountTradeType.PC_TO_FUND){
			return LogType.TYPE_ACCOUNT_PC_TO_FUND;
		}
		if(tradeType>=PcAccountTradeType.ADD_TO_MARGIN && tradeType<=PcAccountTradeType.LEVERAGE_ADD_MARGIN){
			return tradeType-1;
		}
		return null;
	}

	private void sendEventMsg(PcAccountLog logMsg) {
		sender.sendEventMsg(logMsg);
	}

}