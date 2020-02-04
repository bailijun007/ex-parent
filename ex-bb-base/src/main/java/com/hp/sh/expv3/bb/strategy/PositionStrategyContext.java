package com.hp.sh.expv3.bb.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.calc.CompFieldCalc;
import com.hp.sh.expv3.bb.calc.OrderFeeCalc;
import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.component.MarkPriceService;
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.component.vo.BBContractVO;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.TradingRoles;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.module.position.entity.BBPosition;
import com.hp.sh.expv3.bb.msg.BBTradeMsg;
import com.hp.sh.expv3.bb.strategy.common.CommonOrderStrategy;
import com.hp.sh.expv3.bb.strategy.data.OrderTrade;
import com.hp.sh.expv3.bb.strategy.vo.OrderFeeParamVo;
import com.hp.sh.expv3.bb.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.bb.strategy.vo.TradeResult;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;
import com.hp.sh.expv3.utils.math.Precision;

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

	private Map<Integer, StrategyBundle> strategyBundleMap = new HashMap<Integer, StrategyBundle>();
	
	/**
	 * 仓位所需初始保证金
	 * @param pos
	 * @return
	 */
	public BigDecimal calcInitMargin (BBPosition pos){
		long userId = pos.getUserId();
		String asset = pos.getAsset();
		String symbol = pos.getSymbol();
		Integer longFlag = pos.getLongFlag();
		BigDecimal leverage = pos.getLeverage();
        BigDecimal amount = pos.getVolume().multiply(metadataService.getFaceValue(asset, symbol));
        BigDecimal markPrice = markPriceService.getCurrentMarkPrice(asset, symbol);
        
        //新的仓位保证金
        BigDecimal initMarginRatio = feeRatioService.getInitedMarginRatio(leverage); 
        BigDecimal initMargin = _holdPosStrategy.calcInitMargin(longFlag, initMarginRatio, amount, pos.getMeanPrice(), markPrice);
        
        return initMargin;
	}
	
	/**
	 * 计算本单的各项数据
	 * @param order
	 * @param matchedVo
	 * @param bBPosition
	 * @return
	 */
	public TradeResult calcTradeResult(BBTradeMsg matchedVo, BBOrder order, BBPosition bBPosition){
		long userId = order.getUserId();
		String asset = order.getAsset();
		String symbol = order.getSymbol();
		int closeFlag = order.getCloseFlag();
		int longFlag = order.getLongFlag();
		
		OrderRatioData tradeRatioData = orderStrategy.calcRaitoAmt(order, matchedVo.getNumber());
		
		BigDecimal faceValue = this.metadataService.getFaceValue(asset, symbol);
		
		TradeResult tradeResult = new TradeResult();
  
		tradeResult.setVolume(matchedVo.getNumber());
		tradeResult.setPrice(matchedVo.getPrice());
		tradeResult.setAmount(CompFieldCalc.calcAmount(tradeResult.getVolume(), faceValue));
		tradeResult.setBaseValue(CompFieldCalc.calcBaseValue(tradeResult.getVolume(), faceValue, tradeResult.getPrice()));
		tradeResult.setOrderCompleted(BigUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(matchedVo.getNumber())));
		
		//手续费&率
		if(closeFlag==OrderFlag.ACTION_OPEN){
			tradeResult.setFeeRatio(order.getOpenFeeRatio());
			tradeResult.setFee(tradeRatioData.getOpenFee());
			tradeResult.setOrderMargin(tradeRatioData.getOrderMargin());//保证金
		}else{
			BigDecimal closeFeeRatio = this.feeRatioService.getCloseFeeRatio(userId, asset, symbol);
			BigDecimal closeFee = OrderFeeCalc.calcFee(tradeResult.getBaseValue(), closeFeeRatio);

			tradeResult.setFeeRatio(closeFeeRatio);
			tradeResult.setFee(closeFee);
			
			BigDecimal orderMargin = CommonOrderStrategy.slope(tradeResult.getVolume(), bBPosition.getVolume(), bBPosition.getPosMargin());
			tradeResult.setOrderMargin(orderMargin);
		}
		
		//maker fee
		if(matchedVo.getMakerFlag()==TradingRoles.MAKER){
			if(closeFlag==OrderFlag.ACTION_OPEN){
				BigDecimal makerFeeRatio = feeRatioService.getMakerOpenFeeRatio(userId, asset, symbol);
				tradeResult.setMakerFeeRatio(makerFeeRatio);
				tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
			}else{
				BigDecimal makerFeeRatio = feeRatioService.getMakerCloseFeeRatio(userId, asset, symbol);
				tradeResult.setMakerFeeRatio(makerFeeRatio);
				tradeResult.setMakerFee(orderStrategy.calcFee(tradeResult.getBaseValue(), makerFeeRatio));
			}
		}
		
		/* **************** 仓位累计数据 **************** */

		//仓位均价
		if(bBPosition!=null){
			if((closeFlag == OrderFlag.ACTION_OPEN)){
				BigDecimal _newBaseValue = bBPosition.getBaseValue().add(tradeResult.getBaseValue());//一共几个基础货币
				BigDecimal _newVolume = bBPosition.getVolume().add(tradeResult.getVolume());//当前张数
				BigDecimal _newAmount = CompFieldCalc.calcAmount(_newVolume, faceValue); //当前金额
				BigDecimal newMeanPrice = _holdPosStrategy.calcMeanPrice(longFlag, _newBaseValue, _newAmount);
				tradeResult.setNewPosMeanPrice(newMeanPrice);
			}else{
				tradeResult.setNewPosMeanPrice(bBPosition.getMeanPrice());
			}
		}else{
			tradeResult.setNewPosMeanPrice(tradeResult.getPrice());
		}

		//此笔成交收益
		if(IntBool.isTrue(closeFlag) && bBPosition!=null && BigUtils.gt(bBPosition.getMeanPrice(), BigDecimal.ZERO)){
			BigDecimal pnl = _holdPosStrategy.calcPnl(longFlag, tradeRatioData.getAmount(), bBPosition.getMeanPrice(), tradeResult.getPrice());
			tradeResult.setPnl(pnl);	
		}else{
			tradeResult.setPnl(BigDecimal.ZERO);
		}
		
		//强平价
		if(bBPosition!=null){
			BigDecimal _newVolume = IntBool.isTrue(closeFlag)?bBPosition.getVolume().subtract(tradeResult.getVolume()):bBPosition.getVolume().add(tradeResult.getVolume());
			BigDecimal _amount = _newVolume.multiply(order.getFaceValue());
			BigDecimal _newPosMargin = IntBool.isTrue(closeFlag)?bBPosition.getPosMargin().subtract(tradeResult.getOrderMargin()):bBPosition.getPosMargin().add(tradeResult.getOrderMargin());
			tradeResult.setNewPosLiqPrice(
				_holdPosStrategy.calcLiqPrice(longFlag, _amount, tradeResult.getNewPosMeanPrice(), bBPosition.getHoldMarginRatio(), _newPosMargin)
			);
		}else{
			BigDecimal holdRatio = feeRatioService.getHoldRatio(userId, asset, symbol, tradeResult.getVolume());
			tradeResult.setNewPosLiqPrice(
				_holdPosStrategy.calcLiqPrice(longFlag, tradeResult.getAmount(), tradeResult.getNewPosMeanPrice(), holdRatio, tradeResult.getOrderMargin())
			);
		}
		
		return tradeResult;
	}
	
	public BigDecimal calcMaxOpenVolume(Long userId, String asset, String symbol, Long longFlag, BigDecimal leverage, BigDecimal balance){
		BigDecimal latestPrice = this.markPriceService.getLatestPrice(asset, symbol);
		
		BigDecimal faceValue = this.metadataService.getFaceValue(asset, symbol);
		BigDecimal initedMarginRatio = feeRatioService.getInitedMarginRatio(leverage);
		BigDecimal openFeeRatio = feeRatioService.getOpenFeeRatio(userId, asset, symbol);
		BigDecimal closeFeeRatio = feeRatioService.getCloseFeeRatio(userId, asset, symbol);
		
		OrderFeeParamVo orderParam = new OrderFeeParamVo();
		orderParam.setVolume(BigDecimal.ONE);
		orderParam.setFaceValue(faceValue);
		orderParam.setCloseFeeRatio(closeFeeRatio);
		orderParam.setOpenFeeRatio(openFeeRatio);
		orderParam.setMarginRatio(initedMarginRatio);
		orderParam.setPrice(latestPrice);

		OrderRatioData ratioData = orderStrategy.calcOrderAmt(orderParam);
		BigDecimal singleCost = ratioData.getGrossMargin();
		
		BigDecimal vol = balance.divide(singleCost, Precision.INTEGER_PRECISION, Precision.LESS);
		return vol;
	}

	/**
	 * 计算委托成交均价
	 * (v1*f+v2*f+...) / (v1*f/p1+v2*f/p2+...)
	 * 	=
	 * (v1+v2+...) / (v1/p1+v2/p2+...)
	 * 
	 */
	public BigDecimal calcOrderMeanPrice1(String asset, String symbol, int longFlag, List<? extends OrderTrade> tradeList){
		BigDecimal totalVol = BigDecimal.ZERO;
		BigDecimal totalVp = BigDecimal.ZERO;
		
		for(OrderTrade trade : tradeList){
			totalVol.add(trade.getVolume());
			totalVp.add(trade.getVolume().divide(trade.getPrice(), Precision.COMMON_PRECISION, Precision.LESS));
		}
		
		BigDecimal meanPrice = totalVol.divide(totalVp, Precision.COMMON_PRECISION, Precision.LESS);
		return meanPrice;
	}

	public BigDecimal calcOrderMeanPrice(String asset, String symbol, int longFlag, List<? extends OrderTrade> tradeList){
		if(tradeList==null || tradeList.isEmpty()){
			return BigDecimal.ZERO;
		}
		BigDecimal faceValue = BigDecimal.ONE;
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal baseValue = BigDecimal.ZERO;
		HoldPosStrategy strategy = this.getHoldPosStrategy(asset, symbol);
		for(OrderTrade trade : tradeList){
			amount = amount.add(CompFieldCalc.calcAmount(trade.getVolume(), faceValue));
			baseValue = baseValue.add(CompFieldCalc.calcBaseValue(trade.getVolume(), faceValue, trade.getPrice()));
		}
		if(BigUtils.isZero(baseValue)){
			return BigDecimal.ZERO;
		}
		BigDecimal meanPrice = strategy.calcMeanPrice(longFlag, baseValue, amount);
		return meanPrice;
	}
	
	public BigDecimal calcLiqPrice(BBPosition pos) {
		BigDecimal _amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		BigDecimal liqPrice = holdPosStrategy.calcLiqPrice(pos.getLongFlag(), _amount, pos.getMeanPrice(), pos.getHoldMarginRatio(), pos.getPosMargin());
		return liqPrice;
	}
	
	/**
	 * 计算仓位浮动收益
	 */
	public BigDecimal calcFloatingPnl(BBPosition pos){
		HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		BigDecimal _amount = CompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue());
		BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		return holdPosStrategy.calcPnl(pos.getLongFlag(), _amount, pos.getMeanPrice(), markPrice);
	}
	
	/**
	 * 计算仓位保证金率
	 */
	public BigDecimal calPosMarginRatio(BBPosition pos, BigDecimal floatingPnl){
		HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		return holdPosStrategy.calPosMarginRatio(pos.getPosMargin(), floatingPnl, pos.getFaceValue(), pos.getVolume(), markPrice);
	}
	
	int ____________________________;
	
	public HoldPosStrategy getHoldPosStrategy(String asset, String symbol){
		Integer strategyId = this.genStrategyId(asset, symbol);
		StrategyBundle sb = strategyBundleMap.get(strategyId);
		return sb.getHoldPosStrategy();
	}
	
	private Integer genStrategyId(String asset, String symbol){
		BBContractVO pcContract = this.metadataService.getPcContract(asset, symbol);
		return pcContract.getSymbolType();
	}
	
	@Autowired(required=false)
	private void setBundleList(List<StrategyBundle> bundleList){
		for(StrategyBundle sb : bundleList){
			this.strategyBundleMap.put(sb.strategyId(), sb);
		}
	}
	
}
