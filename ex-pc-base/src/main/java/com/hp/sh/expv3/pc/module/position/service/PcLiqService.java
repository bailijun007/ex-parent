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
import com.hp.sh.expv3.pc.module.position.entity.PcLiqRecord;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.module.position.vo.PosUID;
import com.hp.sh.expv3.pc.mq.liq.msg.CancelOrder;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.vo.response.MarkPriceVo;
import com.hp.sh.expv3.utils.DbDateUtils;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;
import com.hp.sh.expv3.utils.math.Precision;

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
    private PcPositionDataService positionDataService;

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
	public LiqHandleResult checkPosLiq(PosUID pos) {
    	PcPosition pcPosition = this.positionDataService.getPosition(pos.getUserId(), pos.getId());
		LiqHandleResult liqResult = new LiqHandleResult();
		
		MarkPriceVo markPriceVo = markPriceService.getLastMarkPrice(pcPosition.getAsset(), pcPosition.getSymbol());
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pcPosition, markPriceVo.getMarkPrice())){
			return liqResult;
		}
		
		//追加保证金
		if(pcPosition.getAutoAddFlag()==IntBool.YES){
			this.pcPositionMarginService.autoAddMargin(pcPosition);
		}
		
		//检查触发强平
		if(!this.checkAndResetLiqStatus(pcPosition, markPriceVo.getMarkPrice())){
			return liqResult;
		}
		
		//强平仓位,修改状态等
		this.lockLiq(pcPosition);
		
		liqResult.setTrigger(true);
		liqResult.setMarkPriceVo(markPriceVo);
		liqResult.setLiqPrice(pcPosition.getLiqPrice());
		liqResult.setPcPosition(pcPosition);
		return liqResult;
		
	}

	/*
	 * 是否触发强平,如果没有改回强平状态
	 * @param pos
	 * @return 是否触发强平
	 */
	private boolean checkAndResetLiqStatus(PcPosition pos, BigDecimal markPrice) {
		if(checkLiqStatus(pos, markPrice)){ //强平
			return true;
		}else{ //不强平
			if(pos.getLiqStatus()!=LiqStatus.NON){
				pos.setLiqStatus(LiqStatus.NON);
				this.positionDataService.update(pos);
			}
			return false;
		}
	}

	/*
	 * 是否触发强平
	 * @param pos
	 * @return 是否触发强平
	 */
	public boolean checkLiqStatus(PcPosition pos, BigDecimal markPrice) {
		BigDecimal _amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal posPnl = holdPosStrategy.calcPnl(pos.getLongFlag(), _amount, pos.getMeanPrice(), markPrice);
		BigDecimal posMarginRatio = holdPosStrategy.calPosMarginRatio(pos.getPosMargin(), posPnl, pos.getFaceValue(), pos.getVolume(), markPrice);
		//保留两位小数
		posMarginRatio = posMarginRatio.setScale(Precision.LIQ_MARGIN_RATIO, Precision.LESS);
		//维持保证金率
		BigDecimal holdMarginRatio = pos.getHoldMarginRatio();
		
		return BigUtils.le(posMarginRatio, holdMarginRatio);
	}
	
	private void lockLiq(PcPosition pos) {
		if(pos.getLiqStatus()!=LiqStatus.FROZEN){
			pos.setLiqStatus(LiqStatus.FROZEN);
			pos.setModified(DbDateUtils.now());
			this.positionDataService.update(pos);
		}
	}

	@LockIt(key="${userId}-${asset}-${symbol}")
	public void cancelCloseOrder(Long userId, String asset, String symbol, Integer longFlag, Long posId, List<CancelOrder> list, Integer lastFlag) {
		PcPosition pos = this.positionDataService.getPosition(userId, asset, symbol, posId);
		
		if(pos.getLiqStatus()==LiqStatus.FORCE_CLOSE){
			logger.warn("仓位已经被强平：posId={}", posId);
			return;
		}
		
		if(pos.getLiqStatus()==LiqStatus.NON){
			logger.warn("仓位强平状态错误：posId={}", posId);
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
	
    @LockIt(key="${pos.userId}-${pos.asset}-${pos.symbol}")
	public void forceClose(PcPosition pos) {
    	this.doLiq(pos);
    }
	
	private void doLiq(PcPosition pos){
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
		positionDataService.update(pos);
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
		record.setMeanPrice(pos.getMeanPrice());
		record.setFee(pos.getCloseFee());
		record.setFeeRatio(feeRatioService.getCloseFeeRatio(pos.getUserId(), pos.getAsset(), pos.getSymbol()));
		
		//save
		pcLiqRecordDAO.save(record);
		publisher.publishEvent(record);
		return record;
	}
	
	private void createLiqOrder(PcLiqRecord record){
		PcPosition pos = positionDataService.getPosition(record.getUserId(), record.getAsset(), record.getSymbol(), record.getPosId());
		this.pcOrderService.create(record.getUserId(), "LIQ-"+record.getId(), record.getAsset(), record.getSymbol(), OrderFlag.ACTION_CLOSE, record.getLongFlag(), TimeInForce.IMMEDIATE_OR_CANCEL, record.getBankruptPrice(), record.getVolume(), pos, IntBool.NO, IntBool.YES);
	}

}
