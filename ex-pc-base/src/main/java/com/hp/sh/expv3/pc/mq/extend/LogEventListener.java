package com.hp.sh.expv3.pc.mq.extend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hp.sh.expv3.pc.constant.LogType;
import com.hp.sh.expv3.pc.constant.PcAccountTradeType;
import com.hp.sh.expv3.pc.module.account.entity.PcAccountRecord;
import com.hp.sh.expv3.pc.module.liq.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.order.entity.PcAccountLog;
import com.hp.sh.expv3.pc.module.order.entity.PcOrderTrade;
import com.hp.sh.expv3.pc.module.order.service.PcAccountLogService;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.service.PcPositionDataService;
import com.hp.sh.expv3.pc.mq.MatchMqSender;
import com.hp.sh.expv3.pc.mq.extend.msg.PcOrderMsg;
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
	private MatchMqSender matchMqSender;
	
	@Autowired
	private PcPositionDataService positionDataService;
	
	@Autowired
	private PcAccountLogService pcAccountLogService;

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void beforeCommit(PcAccountRecord pcAccountRecord) {
		Integer type = this.getType(pcAccountRecord.getTradeType());
		if(type==null){
			return;
		}
		PcAccountLog logMsg = new PcAccountLog();
		logMsg.setUserId(pcAccountRecord.getUserId());
		logMsg.setAsset(pcAccountRecord.getAsset());
		logMsg.setSymbol(null);
		logMsg.setTime(pcAccountRecord.getCreated());
		logMsg.setType(type);
		logMsg.setRefId(pcAccountRecord.getId());
		
		if(NumberUtils.in(type, LogType.TYPE_ACCOUNT_ADD_TO_MARGIN, LogType.TYPE_ACCOUNT_REDUCE_MARGIN, LogType.TYPE_ACCOUNT_AUTO_ADD_MARGIN, LogType.TYPE_ACCOUNT_LEVERAGE_ADD_MARGIN)){
			PcPosition pos = positionDataService.getPosition(pcAccountRecord.getUserId(), pcAccountRecord.getAssociatedId());
			logMsg.setSymbol(pos.getSymbol());
		}
		
		this.sendEventMsg(logMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void beforeCommit(PcOrderTrade orderTrade) {
		PcAccountLog logMsg = new PcAccountLog();
		logMsg.setUserId(orderTrade.getUserId());
		logMsg.setAsset(orderTrade.getAsset());
		logMsg.setSymbol(orderTrade.getSymbol());
		logMsg.setTime(orderTrade.getCreated());
		logMsg.setType(orderTrade.getLogType());
		logMsg.setRefId(orderTrade.getId());
		
		this.sendEventMsg(logMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	public void beforeCommit(PcLiqRecord liqRecord) {
		PcAccountLog logMsg = new PcAccountLog();
		logMsg.setUserId(liqRecord.getUserId());
		logMsg.setAsset(liqRecord.getAsset());
		logMsg.setSymbol(liqRecord.getSymbol());
		logMsg.setTime(liqRecord.getCreated());
		logMsg.setType(IntBool.isTrue(liqRecord.getLongFlag())?LogType.TYPE_LIQ_LONG:LogType.TYPE_LIQ_SHORT);
		logMsg.setRefId(liqRecord.getId());
		
		this.sendEventMsg(logMsg);
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void afterCommit(PcOrderMsg orderMsg) {
		this.matchMqSender.sendPendingNew(orderMsg.getOrder());
	}

	private Integer getType(int tradeType) {
		if(tradeType==PcAccountTradeType.FUND_TO_PC || tradeType==PcAccountTradeType.BB_TO_PC){
			return LogType.TYPE_ACCOUNT_FUND_TO_PC;
		}
		if(tradeType==PcAccountTradeType.PC_TO_FUND || tradeType==PcAccountTradeType.PC_TO_BB){
			return LogType.TYPE_ACCOUNT_PC_TO_FUND;
		}
		if(tradeType>=PcAccountTradeType.ADD_TO_MARGIN && tradeType<=PcAccountTradeType.LEVERAGE_ADD_MARGIN){
			return tradeType-1;
		}
		return null;
	}

	private void sendEventMsg(PcAccountLog accountLog) {
		pcAccountLogService.save(accountLog);
	}

}
