package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.TimeInForce;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.dao.PcLiqRecordDAO;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.mq.liq.LiqMqSender;
import com.hp.sh.expv3.pc.mq.liq.msg.CancelOrder;
import com.hp.sh.expv3.pc.mq.liq.msg.LiqLockMsg;
import com.hp.sh.expv3.pc.strategy.aabb.AABBHoldPosStrategy;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 强平
 * @author wangjg
 *
 */
@Service
public class PcLiqService {

    @Autowired
    private PcPositionService pcPositionService;
    
    @Autowired
    private PcOrderService pcOrderService;
    
    @Autowired
    private PcPositionMarginService pcPositionMarginService;
    
    @Autowired
    private AABBHoldPosStrategy holdPosStrategy;
    
    @Autowired
    private MarkPriceService markPriceService;
    
    @Autowired
    private PcLiqRecordDAO pcLiqRecordDAO;
    
    @Autowired
    private PcPositionDAO pcPositionDAO;
    
    @Autowired
    private LiqMqSender liqMqSender;

	public void handleLiq(PcPosition pos) {
		BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getAsset());
		//检查触发强平
		if(!this.checkLiq(pos, markPrice)){
			return;
		}
		
		//追加保证金
		if(pos.getAutoAddFlag()==IntBool.YES){
			this.pcPositionMarginService.autoAddMargin(pos);
		}
		
		//检查触发强平
		if(!this.checkLiq(pos, markPrice)){
			return;
		}
		
		//强平仓位,修改状态等
		this.lock(pos);
		
		//发送强平消息
		LiqLockMsg lockMsg = new LiqLockMsg();
		lockMsg.setAccountId(pos.getUserId());
		lockMsg.setAsset(pos.getAsset());
		lockMsg.setLiqMarkPrice(markPrice);
		lockMsg.setLiqMarkTime(DbDateUtils.now().getTime());
		lockMsg.setLiqPrice(null);
		lockMsg.setLongFlag(pos.getLongFlag());
		lockMsg.setPosId(pos.getId());
		lockMsg.setSymbol(pos.getSymbol());
		this.liqMqSender.sendLiqLockMsg(lockMsg);
		
	}

	/*
	 * checkAndResetLiqStatus
	 * 是否触发强平
	 * @param pos
	 * @return
	 */
	private boolean checkLiq(PcPosition pos, BigDecimal markPrice) {
		BigDecimal amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal posPnl = holdPosStrategy.calcPosPnl(pos.getLongFlag(), amount, pos.getMeanPrice(), markPrice);
		BigDecimal posMarginRatio = holdPosStrategy.calPosMarginRatio(pos.getPosMargin(), posPnl, pos.getFaceValue(), pos.getVolume(), markPrice);
		BigDecimal holdMarginRatio = pos.getHoldMarginRatio();
		
		if(posMarginRatio.compareTo(holdMarginRatio)<=0){
			return true;
		}else{
			return false;
		}
	}
	
	private void lock(PcPosition pos) {
		this.pcPositionService.lockLiq(pos);
	}

	@Transactional(rollbackFor=Exception.class)
	public void cancelCloseOrder(Long userId, String asset, String symbol, Integer longFlag, Long posId, List<CancelOrder> list) {
		PcPosition pos = this.pcPositionService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(!pos.getId().equals(posId)){
			throw new RuntimeException("平仓，仓位ID与当前仓位ID不一致");
		}
		BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getAsset());
		
		this.checkLiq(pos, markPrice);
		
		for(CancelOrder co : list){
			this.pcOrderService.cancel(userId, asset, symbol, co.getOrderId(), co.getCancelNumber());
		}
		
		this.doLiq(pos);
	}
	
	void doLiq(PcPosition pos){
		PcLiqRecord record = this.saveLiqRecord(pos);
		this.clearLiqPos(pos);
		this.liqOrder(record);
	}
	
	private void clearLiqPos(PcPosition pos){
		pos.setVolume(BigDecimal.ZERO);
		pos.setCloseFee(BigDecimal.ZERO);
		pos.setPosMargin(BigDecimal.ZERO);
		pos.setModified(DbDateUtils.now());
		pcPositionDAO.update(pos);
	}
	
	private PcLiqRecord saveLiqRecord(PcPosition pos){
		Date now = DbDateUtils.now();
		PcLiqRecord record = new PcLiqRecord();
		record.setUserId(pos.getUserId());
		record.setAsset(pos.getAsset());
		record.setSymbol(pos.getSymbol());
		record.setPosId(pos.getId());
		record.setLongFlag(pos.getLongFlag());
		record.setVolume(pos.getVolume());
		record.setPosMargin(pos.getPosMargin());
		BigDecimal amt = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal bankruptPrice = this.holdPosStrategy.calcBankruptPrice(pos.getLongFlag(), pos.getMeanPrice(), amt, pos.getPosMargin());
		record.setBankruptPrice(bankruptPrice);
		record.setCreated(now);
		record.setModified(now);
		pcLiqRecordDAO.save(record);
		return record;
	}
	
	//TODO order.closePosId
	private void liqOrder(PcLiqRecord record){
		this.pcOrderService.create(record.getUserId(), "LIQ-"+record.getId(), record.getAsset(), record.getSymbol(), OrderFlag.ACTION_CLOSE, record.getLongFlag(), TimeInForce.IMMEDIATE_OR_CANCEL, record.getBankruptPrice(), record.getVolume(), IntBool.NO);
	}

	private void cancelLiqOrder(Long userId, Long posId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("posId", posId);
		PcLiqRecord record = this.pcLiqRecordDAO.queryOne(params);
	}

}
