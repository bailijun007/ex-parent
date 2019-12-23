package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.calc.CompFieldCalc;
import com.hp.sh.expv3.pc.calc.OrderFeeCalc;
import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.mq.match.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.pc.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.pc.strategy.vo.TradeResult;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;

@Component
public class PositionStrategyContext {
	
	@Autowired
	private MetadataService metadataService;

	@Autowired
	private MarkPriceService markPriceService;
	
	@Autowired
	private FeeRatioService feeRatioService;

	@Autowired
	private CommonOrderStrategy orderStrategy;

	@Autowired
	private HoldPosStrategy _holdPosStrategy;

	private Map<Integer, StrategyBundle> map;
	
	/**
	 * 仓位所需初始保证金
	 * @param pos
	 * @return
	 */
	public BigDecimal calcInitMargin (PcPosition pos){
		long userId = pos.getUserId();
		String asset = pos.getAsset();
		String symbol = pos.getSymbol();
		Integer longFlag = pos.getLongFlag();
		BigDecimal leverage = pos.getLeverage();
        BigDecimal feeRatio = feeRatioService.getCloseFeeRatio(userId, pos.getAsset(), pos.getSymbol());
        BigDecimal amount = pos.getVolume().multiply(metadataService.getFaceValue(asset, symbol));
        BigDecimal markPrice = markPriceService.getCurrentMarkPrice(asset, symbol);
        
        //新的仓位保证金
        BigDecimal initMarginRatio = feeRatioService.getInitedMarginRatio(leverage); 
        BigDecimal initMargin = _holdPosStrategy.calcInitMargin(longFlag, initMarginRatio, amount, feeRatio, pos.getMeanPrice(), markPrice);
        
        return initMargin;
	}
	
	/**
	 * 计算本单的各项数据
	 * @param order
	 * @param matchedVo
	 * @param pcPosition
	 * @return
	 */
	public TradeResult calcTradeResult(PcTradeMsg matchedVo, PcOrder order, PcPosition pcPosition){
		long userId = order.getUserId();
		String asset = order.getAsset();
		String symbol = order.getSymbol();
		int closeFlag = order.getCloseFlag();
		int longFlag = order.getLongFlag();
		
		OrderRatioData tradeRatioAmt = orderStrategy.calcRaitoAmt(order, matchedVo.getNumber());
		
		BigDecimal faceValue = this.metadataService.getFaceValue(asset, symbol);
		
		TradeResult tradeResult = new TradeResult();
  
		tradeResult.setVolume(matchedVo.getNumber());
		tradeResult.setPrice(matchedVo.getPrice());
		tradeResult.setBaseValue(CompFieldCalc.calcBaseValue(tradeResult.getVolume(), faceValue, tradeResult.getPrice()));
		tradeResult.setOrderCompleted(BigUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(matchedVo.getNumber())));
		
		//手续费&率
		if(closeFlag==OrderFlag.ACTION_OPEN){
			tradeResult.setFeeRatio(order.getOpenFeeRatio());
			tradeResult.setFee(tradeRatioAmt.getOpenFee());
			tradeResult.setOrderMargin(tradeRatioAmt.getOrderMargin());//保证金
		}else{
			BigDecimal closeFeeRatio = this.feeRatioService.getCloseFeeRatio(userId, asset, symbol);
			BigDecimal closeFee = OrderFeeCalc.calcFee(tradeResult.getBaseValue(), closeFeeRatio);

			tradeResult.setFeeRatio(closeFeeRatio);
			tradeResult.setFee(closeFee);
			
			BigDecimal orderMargin = CommonOrderStrategy.slope(tradeResult.getVolume(), pcPosition.getVolume(), pcPosition.getPosMargin());
			tradeResult.setOrderMargin(orderMargin);
		}
		
		//maker fee
		if(closeFlag==OrderFlag.ACTION_OPEN && matchedVo.getMakerFlag()==TradingRoles.MAKER){
			BigDecimal makerFeeRatio = feeRatioService.getMakerOpenFeeRatio(userId, asset, symbol);
			tradeResult.setMakerFeeRatio(makerFeeRatio);
			tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
		}else{
			BigDecimal makerFeeRatio = feeRatioService.getMakerCloseFeeRatio(userId, asset, symbol);
			tradeResult.setMakerFeeRatio(makerFeeRatio);
			tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
		}
		
		/* **************** 仓位累计数据 **************** */

		//仓位均价
		if(pcPosition!=null){
			if((closeFlag == OrderFlag.ACTION_OPEN)){
				BigDecimal _newBaseValue = pcPosition.getBaseValue().add(tradeResult.getBaseValue());//一共几个基础货币
				BigDecimal _newVolume = pcPosition.getVolume().add(tradeResult.getVolume());//当前张数
				BigDecimal _newAmount = CompFieldCalc.calcAmount(_newVolume, faceValue); //当前金额
				BigDecimal newMeanPrice = _holdPosStrategy.calcMeanPrice(longFlag, _newBaseValue, _newAmount);
				tradeResult.setNewPosMeanPrice(newMeanPrice);
			}else{
				tradeResult.setNewPosMeanPrice(pcPosition.getMeanPrice());
			}
		}else{
			tradeResult.setNewPosMeanPrice(tradeResult.getPrice());
		}

		//此笔成交收益
		if(IntBool.isTrue(closeFlag) && pcPosition!=null && BigUtils.gt(pcPosition.getMeanPrice(), BigDecimal.ZERO)){
			BigDecimal pnl = _holdPosStrategy.calcPnl(longFlag, tradeRatioAmt.getAmount(), pcPosition.getMeanPrice(), tradeResult.getPrice());
			tradeResult.setPnl(pnl);	
		}else{
			tradeResult.setPnl(BigDecimal.ZERO);
		}
		
		//强平价
		if(pcPosition!=null){
			BigDecimal amount = pcPosition.getVolume().multiply(order.getFaceValue());
			tradeResult.setNewPosLiqPrice(
				_holdPosStrategy.calcLiqPrice(longFlag, amount, tradeResult.getNewPosMeanPrice(), pcPosition.getHoldMarginRatio(), pcPosition.getPosMargin())
			);
		}else{
			BigDecimal holdRatio = feeRatioService.getHoldRatio(userId, asset, symbol, tradeResult.getVolume());
			tradeResult.setNewPosLiqPrice(
				_holdPosStrategy.calcLiqPrice(longFlag, tradeResult.getAmount(), tradeResult.getNewPosMeanPrice(), holdRatio, tradeResult.getOrderMargin())
			);
		}
		
		return tradeResult;
	}
	
	public HoldPosStrategy getHoldPosStrategy(String asset, String symbol){
		Integer strategyId = this.genStrategyId(asset, symbol);
		StrategyBundle sb = map.get(strategyId);
		return sb.getHoldPosStrategy();
	}
	
	private Integer genStrategyId(String asset, String symbol){
		PcContractVO pcContract = this.metadataService.getPcContract(asset, symbol);
		return pcContract.getSymbolType();
	}
	
	@Autowired(required=false)
	public void setBundleList(List<StrategyBundle> bundleList){
		for(StrategyBundle sb : bundleList){
			this.map.put(sb.strategyId(), sb);
		}
	}
	
}
