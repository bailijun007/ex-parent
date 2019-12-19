package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.calc.OrderFeeCalc;
import com.hp.sh.expv3.pc.component.FeeRatioService;
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
import com.hp.sh.expv3.utils.math.BigUtils;

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
		tradeResult.setPrice(matchedVo.getPrice());
		tradeResult.setAmount(tradeRatioAmt.getAmount());
		tradeResult.setBaseValue(CompFieldCalc.calcBaseValue(tradeResult.getAmount(), tradeResult.getPrice()));
		tradeResult.setOrderCompleted(BigUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(matchedVo.getNumber())));
		
		//手续费&率
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN){
			tradeResult.setFeeRatio(order.getOpenFeeRatio());
			tradeResult.setFee(tradeRatioAmt.getOpenFee());
			tradeResult.setOrderMargin(tradeRatioAmt.getOrderMargin());//保证金
		}else{
			BigDecimal closeFeeRatio = this.feeRatioService.getCloseFeeRatio(order.getUserId(), order.getAsset(), order.getSymbol());
			BigDecimal closeFee = OrderFeeCalc.calcFee(tradeResult.getBaseValue(), closeFeeRatio);

			tradeResult.setFeeRatio(closeFeeRatio);
			tradeResult.setFee(closeFee);
			
			BigDecimal orderMargin = CommonOrderStrategy.slope(tradeResult.getVolume(), pcPosition.getVolume(), pcPosition.getPosMargin());
			tradeResult.setOrderMargin(orderMargin);
		}
		
		//maker fee
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN && matchedVo.getMakerFlag()==TradingRoles.MAKER){
			BigDecimal makerFeeRatio = feeRatioService.getMakerOpenFeeRatio(order.getUserId(), order.getAsset(), order.getSymbol());
			tradeResult.setMakerFeeRatio(makerFeeRatio);
			tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
		}else{
			BigDecimal makerFeeRatio = feeRatioService.getMakerCloseFeeRatio(order.getUserId(), order.getAsset(), order.getSymbol());
			tradeResult.setMakerFeeRatio(makerFeeRatio);
			tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
		}
		
		/* **************** 仓位累计数据 **************** */

		//新的开仓均价
		if(pcPosition!=null){
			BigDecimal newPosBaseValue = pcPosition.getBaseValue().add(tradeResult.getBaseValue());
			BigDecimal newPosVolume = pcPosition.getVolume().add(tradeResult.getVolume());
			BigDecimal newPosAmount = CompFieldCalc.calcAmount(newPosVolume, this.metadataService.getFaceValue(pcPosition.getAsset(), pcPosition.getSymbol()));
			BigDecimal newMeanPrice = PcPriceCalc.calcEntryPrice(IntBool.isTrue(pcPosition.getLongFlag()), newPosBaseValue, newPosAmount);
			tradeResult.setNewPosMeanPrice(newMeanPrice);
		}else{
			tradeResult.setNewPosMeanPrice(tradeResult.getPrice());
		}

		//收益
		if(IntBool.isTrue(order.getCloseFlag()) && pcPosition!=null && BigUtils.gt(pcPosition.getMeanPrice(), BigDecimal.ZERO)){
			BigDecimal pnl = PnlCalc.calcPnl(order.getLongFlag(), tradeRatioAmt.getAmount(), pcPosition.getMeanPrice(), tradeResult.getPrice());
			tradeResult.setPnl(pnl);	
		}else{
			tradeResult.setPnl(BigDecimal.ZERO);
		}
		
		//强平价
		if(pcPosition!=null){
			BigDecimal amount = pcPosition.getVolume().multiply(order.getFaceValue());
			tradeResult.setNewPosliqPrice(
				this.calcLiqPrice(pcPosition.getLongFlag(), amount, tradeResult.getNewPosMeanPrice(), pcPosition.getHoldRatio(), pcPosition.getPosMargin())
			);
		}else{
			BigDecimal holdRatio = feeRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), tradeResult.getVolume());
			tradeResult.setNewPosliqPrice(
				this.calcLiqPrice(order.getLongFlag(), tradeResult.getAmount(), tradeResult.getNewPosMeanPrice(), holdRatio, tradeResult.getOrderMargin())
			);
		}
		
		return tradeResult;
	}
	
	public BigDecimal calcLiqPrice(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		return PcPriceCalc.calcLiqPrice(
			holdMarginRatio, IntBool.isTrue(longFlag), openPrice, amount, posMargin, Precision.COMMON_PRECISION
		);
	}
	
	public BigDecimal calcLiqPrice(String asset, String symbol, int longFlag, BigDecimal volume, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		BigDecimal amount = CompFieldCalc.calcAmount(volume, this.metadataService.getFaceValue(asset, symbol));
		return PcPriceCalc.calcLiqPrice(
			holdMarginRatio, IntBool.isTrue(longFlag), openPrice, amount, posMargin, Precision.COMMON_PRECISION
		);
	}
	
	public String getStrategyId(){
		return "AABB";
	}
	
}
