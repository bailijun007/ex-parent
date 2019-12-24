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
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.TimeInForce;
import com.hp.sh.expv3.pc.module.order.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.dao.PcLiqRecordDAO;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.mq.liq.LiqMqSender;
import com.hp.sh.expv3.pc.mq.liq.msg.CancelOrder;
import com.hp.sh.expv3.pc.mq.liq.msg.LiqLockMsg;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 强平
 * @author wangjg
 *
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcLiqService {

    @Autowired
    private PcPositionService pcPositionService;
    
    @Autowired
    private PcOrderService pcOrderService;
    
    @Autowired
    private PcPositionMarginService pcPositionMarginService;
    
    @Autowired
    private HoldPosStrategy holdPosStrategy;
    
    @Autowired
    private MarkPriceService markPriceService;
    
    @Autowired
    private PcLiqRecordDAO pcLiqRecordDAO;
    
    @Autowired
    private PcPositionDAO pcPositionDAO;
	@Autowired
	private PcOrderDAO pcOrderDAO;
    
    @Autowired
    private LiqMqSender liqMqSender;

	public void handleLiq(PcPosition pos) {
		MarkPriceVo markPriceVo = markPriceService.getLastMarkPrice(pos.getAsset(), pos.getSymbol());
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pos, markPriceVo.getMarkPrice())){
			return;
		}
		
		//追加保证金
		if(pos.getAutoAddFlag()==IntBool.YES){
			this.pcPositionMarginService.autoAddMargin(pos);
		}
		
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pos, markPriceVo.getMarkPrice())){
			return;
		}
		
		//强平仓位,修改状态等
		this.lock(pos);
		
		//发送强平消息
		LiqLockMsg lockMsg = new LiqLockMsg();
		lockMsg.setAccountId(pos.getUserId());
		lockMsg.setAsset(pos.getAsset());
		lockMsg.setLiqMarkPrice(markPriceVo.getMarkPrice());
		lockMsg.setLiqMarkTime(markPriceVo.getTime());
		lockMsg.setLiqPrice(null);
		lockMsg.setLongFlag(pos.getLongFlag());
		lockMsg.setPosId(pos.getId());
		lockMsg.setSymbol(pos.getSymbol());
		this.liqMqSender.sendLiqLockMsg(lockMsg);
		
	}

	/*
	 * 是否触发强平
	 * @param pos
	 * @return 是否触发强平
	 */
	private boolean checkAndResetLiqStatus(PcPosition pos, BigDecimal markPrice) {
		BigDecimal _amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal posPnl = holdPosStrategy.calcPosPnl(pos.getLongFlag(), _amount, pos.getMeanPrice(), markPrice);
		BigDecimal posMarginRatio = holdPosStrategy.calPosMarginRatio(pos.getPosMargin(), posPnl, pos.getFaceValue(), pos.getVolume(), markPrice);
		BigDecimal holdMarginRatio = pos.getHoldMarginRatio();
		
		if(posMarginRatio.compareTo(holdMarginRatio)<=0){//强平
			return true;
		}else{//不强平
			if(pos.getLiqStatus()!=LiqStatus.NO){
				pos.setLiqStatus(LiqStatus.NO);
				this.pcPositionDAO.update(pos);
			}
			return false;
		}
	}
	
	private void lock(PcPosition pos) {
		this.pcPositionService.lockLiq(pos);
	}

	public void cancelCloseOrder(Long userId, String asset, String symbol, Integer longFlag, Long posId, List<CancelOrder> list, Integer lastFlag) {
		PcPosition pos = this.pcPositionService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(!pos.getId().equals(posId)){
			throw new RuntimeException("平仓，仓位ID与当前仓位ID不一致");
		}
		BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getAsset());
		
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pos, markPrice)){
			return;
		}
		
		//取消订单
		for(CancelOrder co : list){
			this.pcOrderService.cancel(userId, asset, symbol, co.getOrderId(), co.getCancelNumber());
		}
		
		//强平
		if(IntBool.isTrue(lastFlag)){
			this.doLiq(pos);
		}
	}
	
	void doLiq(PcPosition pos){
		//保存强平记录
		PcLiqRecord record = this.saveLiqRecord(pos);
		//清空仓位
		this.clearLiqPos(pos);
		//创建强平委托
		this.createLiqOrder(record);
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
		BigDecimal _amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal bankruptPrice = this.holdPosStrategy.calcBankruptPrice(pos.getLongFlag(), pos.getMeanPrice(), _amount, pos.getPosMargin());
		record.setBankruptPrice(bankruptPrice);
		record.setCreated(now);
		record.setModified(now);
		pcLiqRecordDAO.save(record);
		return record;
	}
	
	private void createLiqOrder(PcLiqRecord record){
		PcPosition pos = this.pcPositionDAO.findById(record.getUserId(), record.getPosId());
		this.pcOrderService.create(record.getUserId(), "LIQ-"+record.getId(), record.getAsset(), record.getSymbol(), OrderFlag.ACTION_CLOSE, record.getLongFlag(), TimeInForce.IMMEDIATE_OR_CANCEL, record.getBankruptPrice(), record.getVolume(), pos, IntBool.NO, IntBool.YES);
	}
	
	public void handleLiqTrade(Long userId, Long recordId){
		PcLiqRecord record = this.pcLiqRecordDAO.findById(userId, recordId);
		
	}

	private void cancelLiqOrder(Long userId, Long posId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("closePosId", posId);
		PcOrder liqOrder = this.pcOrderDAO.queryOne(params);
		this.pcOrderService.cancel(userId, liqOrder.getAsset(), liqOrder.getSymbol(), liqOrder.getId(), liqOrder.getVolume().subtract(liqOrder.getFilledVolume()));
	}

}
