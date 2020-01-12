package com.hp.sh.expv3.pc.module.position.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hp.sh.expv3.commons.lock.LockIt;
import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.constant.LiqStatus;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.TimeInForce;
import com.hp.sh.expv3.pc.job.LiqHandleResult;
import com.hp.sh.expv3.pc.module.order.service.PcOrderService;
import com.hp.sh.expv3.pc.module.position.dao.PcLiqRecordDAO;
import com.hp.sh.expv3.pc.module.position.dao.PcPositionDAO;
import com.hp.sh.expv3.pc.module.position.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.mq.liq.msg.CancelOrder;
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
	private static final Logger logger = LoggerFactory.getLogger(PcLiqService.class);
    

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
	private FeeRatioService feeRatioService;
    
    @Autowired
    private ApplicationEventPublisher publisher;

    @LockIt(key="${pos.userId}-${pos.asset}-${pos.symbol}")
	public LiqHandleResult handleLiq(PcPosition pos) {
		LiqHandleResult liqResult = new LiqHandleResult();
		
		MarkPriceVo markPriceVo = markPriceService.getLastMarkPrice(pos.getAsset(), pos.getSymbol());
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pos, markPriceVo.getMarkPrice())){
			return liqResult;
		}
		
		//追加保证金
		if(pos.getAutoAddFlag()==IntBool.YES){
			this.pcPositionMarginService.autoAddMargin(pos);
		}
		
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pos, markPriceVo.getMarkPrice())){
			return liqResult;
		}
		
		//强平仓位,修改状态等
		this.lockLiq(pos);
		
		liqResult.setTrigger(true);
		liqResult.setMarkPriceVo(markPriceVo);
		liqResult.setLiqPrice(pos.getLiqPrice());
		return liqResult;
		
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
		
		if(posMarginRatio.compareTo(holdMarginRatio)<=0){ //强平
			return true;
		}else{ //不强平
			if(pos.getLiqStatus()!=LiqStatus.NON){
				pos.setLiqStatus(LiqStatus.NON);
				this.pcPositionService.update(pos);
			}
			return false;
		}
	}
	
	private void lockLiq(PcPosition pos) {
		if(pos.getLiqStatus()!=LiqStatus.FROZEN){
			pos.setLiqStatus(LiqStatus.FROZEN);
			pos.setModified(DbDateUtils.now());
			this.pcPositionService.update(pos);
		}
	}

	@LockIt(key="${userId}-${asset}-${symbol}")
	public void cancelCloseOrder(Long userId, String asset, String symbol, Integer longFlag, Long posId, List<CancelOrder> list, Integer lastFlag) {
		PcPosition pos = this.pcPositionService.getCurrentPosition(userId, asset, symbol, longFlag);
		if(pos==null){
			logger.error("当前仓位不存在!userId={}, symbol={}, longFlag={}。  强平仓位:{}", userId, symbol, longFlag, posId);
			return;
		}
		if(!pos.getId().equals(posId)){
			throw new RuntimeException("平仓，仓位ID与当前仓位ID不一致");
		}
		//强平过了
		if(pos.getLiqStatus()==LiqStatus.FORCE_CLOSE){
			return;
		}
		BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pos, markPrice)){
			return;
		}
		
		//取消订单
		for(CancelOrder co : list){
			this.pcOrderService.cance4Liq(userId, asset, symbol, co.getOrderId(), co.getCancelNumber());
		}
		
		//强平
		if(IntBool.isTrue(lastFlag)){
			this.doLiq(pos);
		}
	}
	
	void doLiq(PcPosition pos){
		Long now = DbDateUtils.now();
		//1、保存强平记录
		PcLiqRecord record = this.saveLiqRecord(pos, now);
		//2、清空仓位
		this.clearLiqPos(pos, now);
		//3、创建强平委托
//		this.createLiqOrder(record);
	}
	
	private void clearLiqPos(PcPosition pos, Long now){
		pos.setVolume(BigDecimal.ZERO);
		pos.setCloseFee(BigDecimal.ZERO);
		pos.setPosMargin(BigDecimal.ZERO);
		pos.setLiqStatus(LiqStatus.FORCE_CLOSE);
		pos.setModified(now);
		pcPositionService.update(pos);
	}
	
	private PcLiqRecord saveLiqRecord(PcPosition pos, Long now){
		PcLiqRecord record = new PcLiqRecord();
		record.setUserId(pos.getUserId());
		record.setAsset(pos.getAsset());
		record.setSymbol(pos.getSymbol());
		record.setPosId(pos.getId());
		record.setLongFlag(pos.getLongFlag());
		record.setVolume(pos.getVolume());
		record.setFilledVolume(BigDecimal.ZERO);
		record.setPosMargin(pos.getPosMargin());
		BigDecimal _amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal bankruptPrice = this.holdPosStrategy.calcBankruptPrice(pos.getLongFlag(), pos.getMeanPrice(), _amount, pos.getPosMargin());
		record.setBankruptPrice(bankruptPrice);
		record.setCreated(now);
		record.setModified(now);
		
		//log
		record.setLiqPrice(pos.getLiqPrice());
		record.setFee(pos.getCloseFee());
		record.setFeeRatio(feeRatioService.getCloseFeeRatio(pos.getUserId(), pos.getAsset(), pos.getSymbol()));
		
		//save
		pcLiqRecordDAO.save(record);
		publisher.publishEvent(record);
		return record;
	}
	
	private void createLiqOrder(PcLiqRecord record){
		PcPosition pos = pcPositionService.getPosition(record.getUserId(), record.getAsset(), record.getSymbol(), record.getPosId());
		this.pcOrderService.create(record.getUserId(), "LIQ-"+record.getId(), record.getAsset(), record.getSymbol(), OrderFlag.ACTION_CLOSE, record.getLongFlag(), TimeInForce.IMMEDIATE_OR_CANCEL, record.getBankruptPrice(), record.getVolume(), pos, IntBool.NO, IntBool.YES);
	}

}
