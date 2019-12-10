package com.hp.sh.expv3.pc.strategy.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.calc.MarginFeeCalc;
import com.hp.sh.expv3.pc.calc.PcPriceCalc;
import com.hp.sh.expv3.pc.calc.PnlCalc;
import com.hp.sh.expv3.pc.component.MarginRatioService;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.Precision;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.mq.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.PositionStrategy;
import com.hp.sh.expv3.pc.strategy.vo.OrderAmount;
import com.hp.sh.expv3.pc.strategy.vo.TradeData;
import com.hp.sh.expv3.utils.IntBool;

/**
 * 
 * @author wangjg
 *
 */
@Component
public class AABBPositionStrategy implements PositionStrategy {
	
	@Autowired
	private MarginRatioService marginRatioService;
	
	@Autowired
	private PcPriceCalc pcPriceCalc;
	
	@Autowired
	private CommonOrderStrategy orderStrategy;
	
	/**
	 * 按比例计算订单的各种金额、比率和费用
	 * @param order 订单数据
	 * @param volume 分量
	 * @param closeFeeRaito 
	 * @return 
	 */
	public TradeData getTradeData(PcOrder order, PcTradeMsg matchedVo, PcPosition pcPosition){
		OrderAmount tradeRatioAmt = orderStrategy.calcRaitoAmt(order, matchedVo.getNumber());
		
		TradeData tradeData = new TradeData();

		tradeData.setVolume(matchedVo.getNumber());
		tradeData.setAmount(tradeRatioAmt.getAmount());
		tradeData.setBaseValue(tradeRatioAmt.getBaseValue());
		tradeData.setOrderMargin(tradeRatioAmt.getOrderMargin());//保证金
		tradeData.setCompleted(BigMath.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(matchedVo.getNumber())));
		
		if(order.getCloseFlag()==OrderFlag.ACTION_OPEN){
			tradeData.setFeeRatio(order.getOpenFeeRatio());
			tradeData.setFee(tradeRatioAmt.getOpenFee());
		}else{
			BigDecimal closeFeeRatio = this.marginRatioService.getCloseFeeRatio(order.getUserId());
			BigDecimal closeFee = MarginFeeCalc.calcFee(tradeData.getBaseValue(), closeFeeRatio);

			tradeData.setFeeRatio(closeFeeRatio);
			tradeData.setFee(closeFee);
		}

		//新的开仓均价
		if(pcPosition!=null){
			BigDecimal newMeanPrice = pcPriceCalc.calcEntryPrice(IntBool.isTrue(order.getLongFlag()), pcPosition.getBaseValue(), tradeData.getAmount(), Precision.COMMON_PRECISION);
			tradeData.setNewMeanPrice(newMeanPrice);			
		}else{
			tradeData.setNewMeanPrice(matchedVo.getPrice());
		}

		if(IntBool.isTrue(order.getCloseFlag()) && pcPosition!=null && BigMath.gt(pcPosition.getMeanPrice(), BigDecimal.ZERO)){
			BigDecimal pnl = PnlCalc.calcPnl(order.getLongFlag(), tradeRatioAmt.getAmount(), pcPosition.getMeanPrice(), matchedVo.getPrice(), Precision.COMMON_PRECISION);
			tradeData.setPnl(pnl);	
		}else{
			tradeData.setPnl(BigDecimal.ZERO);
		}
		
		//强平价
		if(pcPosition!=null){
			tradeData.setLiqPrice(
				pcPriceCalc.calcLiqPrice(
					pcPosition.getHoldRatio(),
					IntBool.isTrue(pcPosition.getLongFlag()), 
					tradeData.getNewMeanPrice(), 
					tradeData.getAmount(),
					pcPosition.getPosMargin(), 
					Precision.COMMON_PRECISION)
			);
		}else{
			BigDecimal holdRatio = marginRatioService.getHoldRatio(order.getUserId(), order.getAsset(), order.getSymbol(), matchedVo.getNumber());
			tradeData.setLiqPrice(
					pcPriceCalc.calcLiqPrice(
						holdRatio,
						IntBool.isTrue(order.getLongFlag()), 
						tradeData.getNewMeanPrice(), 
						tradeData.getAmount(),
						tradeData.getOrderMargin(), 
						Precision.COMMON_PRECISION)
				);
		}
		
		tradeData.setAvgCostPrice(BigDecimal.ZERO);
		
		return tradeData;
	}
	
}
