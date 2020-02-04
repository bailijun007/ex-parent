package com.hp.sh.expv3.bb.mq.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hp.sh.expv3.bb.constant.LogType;
import com.hp.sh.expv3.bb.constant.BBAccountTradeType;
import com.hp.sh.expv3.bb.module.account.entity.BBAccountRecord;
import com.hp.sh.expv3.bb.module.order.entity.BBOrderTrade;
import com.hp.sh.expv3.bb.module.position.entity.BBLiqRecord;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.module.position.service.BBPositionDataService;
import com.hp.sh.expv3.bb.msg.BBAccountLog;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.NumberUtils;

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
	
	@Autowired
	private BBPositionDataService positionDataService;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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
		
		if(NumberUtils.in(type, LogType.TYPE_ACCOUNT_ADD_TO_MARGIN, LogType.TYPE_ACCOUNT_REDUCE_MARGIN, LogType.TYPE_ACCOUNT_AUTO_ADD_MARGIN, LogType.TYPE_ACCOUNT_LEVERAGE_ADD_MARGIN)){
			BBPosition pos = positionDataService.getPosition(bBAccountRecord.getUserId(), bBAccountRecord.getAssociatedId());
			logMsg.setSymbol(pos.getSymbol());
		}
		
		this.sendEventMsg(logMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
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

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(BBLiqRecord liqRecord) {
		BBAccountLog logMsg = new BBAccountLog();
		logMsg.setUserId(liqRecord.getUserId());
		logMsg.setAsset(liqRecord.getAsset());
		logMsg.setSymbol(liqRecord.getSymbol());
		logMsg.setTime(liqRecord.getCreated());
		logMsg.setType(IntBool.isTrue(liqRecord.getLongFlag())?LogType.TYPE_LIQ_LONG:LogType.TYPE_LIQ_SHORT);
		logMsg.setRefId(liqRecord.getId());
		
		this.sendEventMsg(logMsg);
	}

	private Integer getType(int tradeType) {
		if(tradeType==BBAccountTradeType.FUND_TO_PC){
			return LogType.TYPE_ACCOUNT_FUND_TO_PC;
		}
		if(tradeType==BBAccountTradeType.PC_TO_FUND){
			return LogType.TYPE_ACCOUNT_PC_TO_FUND;
		}
		if(tradeType>=BBAccountTradeType.ADD_TO_MARGIN && tradeType<=BBAccountTradeType.LEVERAGE_ADD_MARGIN){
			return tradeType-1;
		}
		return null;
	}

	private void sendEventMsg(BBAccountLog logMsg) {
		sender.sendEventMsg(logMsg);
	}

}
