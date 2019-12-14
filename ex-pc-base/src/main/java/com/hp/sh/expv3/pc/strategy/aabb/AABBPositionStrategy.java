package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.calc.MarginFeeCalc;
import com.hp.sh.expv3.pc.calc.PnlCalc;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.constant.BidFlag;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.mq.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.PositionStrategy;
import com.hp.sh.expv3.pc.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.pc.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.pc.strategy.vo.TradeResult;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigMathUtils;

/**
 * 
 * @author wangjg
 *
 */
@Component
public class AABBPositionStrategy implements PositionStrategy {
	
	@Autowired
	private FeeRatioService feeRatioService;
	
	@Autowired
	private CommonOrderStrategy orderStrategy;
	
	@Autowired
	private AABBMetadataService metadataService;
	
	
	/**
	 * 计算本单的各项数据
	 * @param order
	 * @param matchedVo
	 * @param pcPosition
	 * @return
	 */
	public TradeResult getTradeResult(PcOrder order, PcTradeMsg matchedVo, PcPosition pcPosition){
		OrderRatioData tradeRatioAmt = orderStrategy.calcRaitoAmt(order, matchedVo.getNumber());
		
		TradeResult tradeResult = new TradeResult();
  
		tradeResult.setVolume(matchedVo.getNumber());
		tradeResult.setAmount(tradeRatioAmt.getAmount());
		tradeResult.setBaseValue(CompFieldCalc.calcBaseValue(tradeResult.getAmount(), matchedVo.getPrice()));
		tradeResult.setOrderMargin(tradeRatioAmt.getOrderMargin());//保证金
		tradeResult.setOrderCompleted(BigMathUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(matchedVo.getNumber())));
		
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN){
			tradeResult.setFeeRatio(order.getOpenFeeRatio());
			tradeResult.setFee(tradeRatioAmt.getOpenFee());
		}else{
			BigDecimal closeFeeRatio = this.feeRatioService.getCloseFeeRatio(order.getUserId());
			BigDecimal closeFee = MarginFeeCalc.calcFee(tradeResult.getBaseValue(), closeFeeRatio);

			tradeResult.setFeeRatio(closeFeeRatio);
			tradeResult.setFee(closeFee);
		}

		//新的开仓均价
		if(pcPosition!=null){
			BigDecimal newMeanPrice = PcPriceCalc.calcEntryPrice(IntBool.isTrue(order.getLongFlag()), pcPosition.getBaseValue(), tradeResult.getAmount(), Precision.COMMON_PRECISION);
			tradeResult.setNewMeanPrice(newMeanPrice);			
		}else{
			tradeResult.setNewMeanPrice(matchedVo.getPrice());
		}

		if(IntBool.isTrue(order.getCloseFlag()) && pcPosition!=null && BigMathUtils.gt(pcPosition.getMeanPrice(), BigDecimal.ZERO)){
			BigDecimal pnl = PnlCalc.calcPnl(order.getLongFlag(), tradeRatioAmt.getAmount(), pcPosition.getMeanPrice(), matchedVo.getPrice(), Precision.COMMON_PRECISION);
			tradeResult.setPnl(pnl);	
		}else{
			tradeResult.setPnl(BigDecimal.ZERO);
		}
		
		//强平价
		if(pcPosition!=null){
			BigDecimal amount = pcPosition.getVolume().multiply(order.getFaceValue());
			tradeResult.setLiqPrice(
				this.calcLiqPrice(pcPosition.getLongFlag(), amount, tradeResult.getNewMeanPrice(), pcPosition.getHoldRatio(), pcPosition.getPosMargin())
			);
		}else{
			BigDecimal holdRatio = feeRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), matchedVo.getNumber());
			tradeResult.setLiqPrice(
				this.calcLiqPrice(order.getLongFlag(), tradeResult.getAmount(), tradeResult.getNewMeanPrice(), holdRatio, tradeResult.getOrderMargin())
			);
		}
		
		//maker fee
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN && matchedVo.getMakerFlag()==TradingRoles.MAKER){
			BigDecimal makerFeeRatio = feeRatioService.getMakerOpenFeeRatio(order.getUserId());
			tradeResult.setMakerFeeRatio(makerFeeRatio);
			tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
		}else{
			BigDecimal makerFeeRatio = feeRatioService.getMakerCloseFeeRatio(order.getUserId());
			tradeResult.setMakerFeeRatio(makerFeeRatio);
			tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
		}
		
		return tradeResult;
	}
	
	public BigDecimal calcLiqPrice(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		return PcPriceCalc.calcLiqPrice(
			holdMarginRatio, IntBool.isTrue(longFlag), openPrice, amount, posMargin, Precision.COMMON_PRECISION
		);
	}
	
	public BigDecimal calcLiqPrice(String symbol, int longFlag, BigDecimal volume, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		BigDecimal amount = CompFieldCalc.calcAmount(volume, this.metadataService.getFaceValue(symbol));
		return PcPriceCalc.calcLiqPrice(
			holdMarginRatio, IntBool.isTrue(longFlag), openPrice, amount, posMargin, Precision.COMMON_PRECISION
		);
	}
	
	public String getStrategyId(){
		return "AABB";
	}
	
}
