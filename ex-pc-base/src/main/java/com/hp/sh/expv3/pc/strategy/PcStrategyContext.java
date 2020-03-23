package com.hp.sh.expv3.pc.strategy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.component.FeeRatioService;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.component.MetadataService;
import com.hp.sh.expv3.pc.component.vo.PcContractVO;
import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.constant.TradingRoles;
import com.hp.sh.expv3.pc.module.order.entity.PcOrder;
import com.hp.sh.expv3.pc.module.position.entity.PcPosition;
import com.hp.sh.expv3.pc.msg.PcTradeMsg;
import com.hp.sh.expv3.pc.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.pc.strategy.data.OrderTrade;
import com.hp.sh.expv3.pc.strategy.data.PosBaseData;
import com.hp.sh.expv3.pc.strategy.data.PosData;
import com.hp.sh.expv3.pc.strategy.vo.OrderFeeData;
import com.hp.sh.expv3.pc.strategy.vo.OrderFeeParamVo;
import com.hp.sh.expv3.pc.strategy.vo.OrderMarginVo;
import com.hp.sh.expv3.pc.strategy.vo.TradeResult;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.BigUtils;
import com.hp.sh.expv3.utils.math.Precision;

@Component
public class PcStrategyContext {
	
	@Autowired
	private MetadataService metadataService;

	@Autowired
	private MarkPriceService markPriceService;
	
	@Autowired
	private FeeRatioService feeRatioService;

	private Map<Integer, StrategyBundle> strategyBundleMap = new HashMap<Integer, StrategyBundle>();
	
	int _____________order_________________;
	
	/**
	 * 计算交易合约 基础货币总价值
	 */
	public BigDecimal calcBaseValue(String asset, String symbol, BigDecimal volume, BigDecimal faceValue, BigDecimal price){
		OrderStrategy os = this.getOrderStrategy(asset, symbol);
		return os.calcBaseValue(volume, faceValue, price);
	}
	
	public OrderFeeData calcNewOrderFee(String asset, String symbol, OrderFeeParam pcOrder){
		OrderStrategy os = this.getOrderStrategy(asset, symbol);
		return os.calcNewOrderFee(pcOrder);
	}
	
	/**
	 * 计算仓位维持保证金
	 * @param pos
	 * @return
	 */
	public BigDecimal calHoldMargin(PosData pos){
		OrderStrategy os = this.getOrderStrategy(pos.getAsset(), pos.getSymbol());
		return os.calMargin(pos.getVolume(), pos.getFaceValue(), pos.getMeanPrice(), pos.getHoldMarginRatio());
	}
	
	int ______________pos_______________;
	
	/**
	 * 仓位所需初始保证金
	 * @param pos
	 * @return
	 */
	public BigDecimal calcInitMargin (PosData pos){
		BigDecimal leverage = pos.getLeverage();
		return this.calcInitMargin(pos, leverage);
	}
	
	/**
	 * 仓位使用指定杠杆所需的初始保证金
	 * @param pos
	 * @return
	 */
	public BigDecimal calcInitMargin (PosData pos, BigDecimal leverage){
		String asset = pos.getAsset();
		String symbol = pos.getSymbol();
		Integer longFlag = pos.getLongFlag();
		
		//标记价格
        BigDecimal markPrice = markPriceService.getCurrentMarkPrice(asset, symbol);
        
        //新的仓位保证金
        BigDecimal initMarginRatio = feeRatioService.getInitedMarginRatio(leverage); 
        
        HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(asset, symbol);
        BigDecimal initMargin = holdPosStrategy.calcInitMargin(longFlag, initMarginRatio, pos.getVolume(), pos.getFaceValue(), pos.getMeanPrice(), markPrice);
        
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
		
		OrderStrategy orderStrategy = this.getOrderStrategy(asset, symbol);
        HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(asset, symbol);
        
		BigDecimal faceValue = this.metadataService.getFaceValue(asset, symbol);
		
		TradeResult tradeResult = new TradeResult();
  
		tradeResult.setNumber(matchedVo.getNumber());
		tradeResult.setPrice(matchedVo.getPrice());
		tradeResult.setAmount(orderStrategy.calcAmount(tradeResult.getNumber(), faceValue, tradeResult.getPrice()));
		tradeResult.setBaseValue(orderStrategy.calcBaseValue(tradeResult.getNumber(), faceValue, tradeResult.getPrice()));
		tradeResult.setOrderCompleted(BigUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(matchedVo.getNumber())));
		
		//手续费率
		if(matchedVo.getMakerFlag()==TradingRoles.MAKER){
			BigDecimal feeRatio = feeRatioService.getMakerFeeRatio(userId, asset, symbol);
			tradeResult.setFeeRatio(feeRatio);
		}else{
			if(closeFlag==OrderFlag.ACTION_OPEN){
				tradeResult.setFeeRatio(order.getOpenFeeRatio());
			}else{
				BigDecimal feeRatio = this.feeRatioService.getTakerFeeRatio(userId, asset, symbol);
				tradeResult.setFeeRatio(feeRatio);
			}
		}

		// 手续费&保证金
		if(closeFlag==OrderFlag.ACTION_OPEN){
			OrderFeeData feeData = orderStrategy.calcRaitoFee(order, order.getVolume(), matchedVo.getNumber());
			if(BigUtils.isZero(order.getOpenFeeRatio())){
				tradeResult.setFee(BigDecimal.ZERO);
			}else{
				tradeResult.setFee(feeData.getOpenFee().multiply(tradeResult.getFeeRatio().divide(order.getOpenFeeRatio(), Precision.PERCENT_PRECISION, Precision.MORE)));
			}
			
			tradeResult.setOrderMargin(feeData.getOrderMargin()); //保证金
			tradeResult.setOrderCloseFee(feeData.getCloseFee());
		}else{
			BigDecimal tradeFee = orderStrategy.calcTradeFee(tradeResult.getNumber(), faceValue, tradeResult.getPrice(), tradeResult.getFeeRatio());
			if(BigUtils.isZero(order.getOpenFeeRatio())){
				tradeResult.setFee(BigDecimal.ZERO);
			}else{
				tradeResult.setFee(tradeFee.multiply(tradeResult.getFeeRatio().divide(order.getOpenFeeRatio(), Precision.PERCENT_PRECISION, Precision.MORE)));
			}
			
			OrderMarginVo posMargin = new OrderMarginVo(pcPosition.getPosMargin(), BigDecimal.ZERO, pcPosition.getCloseFee());
			tradeResult.setOrderMargin(posMargin.getOrderMargin()); //保证金
			tradeResult.setOrderCloseFee(BigDecimal.ZERO);
		}
		
		/* **************** 仓位累计数据 **************** */
        
		//仓位均价
		if(pcPosition!=null){
			if((closeFlag == OrderFlag.ACTION_OPEN)){
				BigDecimal _newBaseValue = pcPosition.getBaseValue().add(tradeResult.getBaseValue());//一共几个基础货币
				BigDecimal _newVolume = pcPosition.getVolume().add(tradeResult.getNumber());//当前张数
				BigDecimal _newAmount = orderStrategy.calcAmount(_newVolume, faceValue, tradeResult.getPrice()); //当前金额

				BigDecimal newMeanPrice = holdPosStrategy.calcMeanPrice(longFlag, _newBaseValue, _newAmount);
				tradeResult.setNewPosMeanPrice(newMeanPrice);
			}else{
				tradeResult.setNewPosMeanPrice(pcPosition.getMeanPrice());
			}
		}else{
			tradeResult.setNewPosMeanPrice(tradeResult.getPrice());
		}

		//此笔成交收益
		if(IntBool.isTrue(closeFlag) && pcPosition!=null && BigUtils.gt(pcPosition.getMeanPrice(), BigDecimal.ZERO)){
			BigDecimal pnl = holdPosStrategy.calcPnl(longFlag, matchedVo.getNumber(), faceValue, pcPosition.getMeanPrice(), tradeResult.getPrice());
			tradeResult.setPnl(pnl);	
		}else{
			tradeResult.setPnl(BigDecimal.ZERO);
		}
		
		//强平价
		if(pcPosition!=null){
			BigDecimal _newVolume = IntBool.isTrue(closeFlag)?pcPosition.getVolume().subtract(tradeResult.getNumber()):pcPosition.getVolume().add(tradeResult.getNumber());
			BigDecimal _newPosMargin = IntBool.isTrue(closeFlag)?pcPosition.getPosMargin().subtract(tradeResult.getOrderMargin()):pcPosition.getPosMargin().add(tradeResult.getOrderMargin());
			tradeResult.setNewPosLiqPrice(
				holdPosStrategy.calcLiqPrice(longFlag, order.getFaceValue(), _newVolume, tradeResult.getNewPosMeanPrice(), pcPosition.getHoldMarginRatio(), _newPosMargin)
			);
		}else{
			BigDecimal holdRatio = feeRatioService.getHoldRatio(userId, asset, symbol, tradeResult.getNumber());
			tradeResult.setNewPosLiqPrice(
				holdPosStrategy.calcLiqPrice(longFlag, order.getFaceValue(), tradeResult.getNumber(), tradeResult.getNewPosMeanPrice(), holdRatio, tradeResult.getOrderMargin())
			);
		}
		
		return tradeResult;
	}
	
	public BigDecimal calcMaxOpenVolume(Long userId, String asset, String symbol, Long longFlag, BigDecimal leverage, BigDecimal balance){
		BigDecimal latestPrice = this.markPriceService.getLatestPrice(asset, symbol);
		
		BigDecimal faceValue = this.metadataService.getFaceValue(asset, symbol);
		BigDecimal initedMarginRatio = feeRatioService.getInitedMarginRatio(leverage);
		BigDecimal openFeeRatio = feeRatioService.getTakerFeeRatio(userId, asset, symbol);
		BigDecimal closeFeeRatio = feeRatioService.getTakerFeeRatio(userId, asset, symbol);
		
		OrderFeeParamVo orderParam = new OrderFeeParamVo();
		orderParam.setVolume(BigDecimal.ONE);
		orderParam.setFaceValue(faceValue);
		orderParam.setCloseFeeRatio(closeFeeRatio);
		orderParam.setOpenFeeRatio(openFeeRatio);
		orderParam.setMarginRatio(initedMarginRatio);
		orderParam.setPrice(latestPrice);

		OrderStrategy orderStrategy = this.getOrderStrategy(asset, symbol);
		OrderFeeData orderFee = orderStrategy.calcNewOrderFee(orderParam);
		BigDecimal singleCost = orderFee.getGrossMargin();
		
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
		HoldPosStrategy hps = this.getHoldPosStrategy(asset, symbol);
		OrderStrategy ops = this.getOrderStrategy(asset, symbol);
		for(OrderTrade trade : tradeList){
			amount = amount.add(ops.calcAmount(trade.getVolume(), faceValue, trade.getPrice()));
			baseValue = baseValue.add(ops.calcBaseValue(trade.getVolume(), faceValue, trade.getPrice()));
		}
		if(BigUtils.isZero(baseValue)){
			return BigDecimal.ZERO;
		}
		BigDecimal meanPrice = hps.calcMeanPrice(longFlag, baseValue, amount);
		return meanPrice;
	}
	
	public BigDecimal calcLiqPrice(PosData pos) {
		HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		BigDecimal liqPrice = holdPosStrategy.calcLiqPrice(pos.getLongFlag(), pos.getFaceValue(), pos.getVolume(), pos.getMeanPrice(), pos.getHoldMarginRatio(), pos.getPosMargin());
		return liqPrice;
	}
	
	/**
	 * 计算仓位浮动收益
	 */
	public BigDecimal calcFloatingPnl(PosData pos) {
		BigDecimal markPrice = this.markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		 HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		 return holdPosStrategy.calcPnl(pos.getLongFlag(), pos.getVolume(), pos.getFaceValue(), pos.getMeanPrice(), markPrice);
	}
	
	/**
	 * 计算收益
	 */
	public BigDecimal calcPnl(String asset, String symbol, int longFlag, BigDecimal faceValue, BigDecimal volume, BigDecimal openPrice, BigDecimal closePrice) {
		HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(asset, symbol);
		return holdPosStrategy.calcPnl(longFlag, volume, faceValue, openPrice, closePrice);
	}

	/**
	 * 计算仓位保证金率
	 */
	public BigDecimal calPosMarginRatio(PosData pos, BigDecimal floatingPnl){
		HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		BigDecimal markPrice = markPriceService.getCurrentMarkPrice(pos.getAsset(), pos.getSymbol());
		return holdPosStrategy.calPosMarginRatio(pos.getPosMargin(), pos.getFaceValue(), pos.getVolume(), floatingPnl, markPrice);
	}
	
	public BigDecimal calcBankruptPrice(PosBaseData calcParam){
		HoldPosStrategy hs = this.getHoldPosStrategy(calcParam.getAsset(), calcParam.getSymbol());
		return hs.calcBankruptPrice(calcParam.getLongFlag(), calcParam.getVolume(), calcParam.getFaceValue(), calcParam.getPosMargin(), calcParam.getMeanPrice());
	}
	
	public BigDecimal calPosMarginRatio(PosData pos, BigDecimal posPnl, BigDecimal markPrice) {
		 HoldPosStrategy holdPosStrategy = this.getHoldPosStrategy(pos.getAsset(), pos.getSymbol());
		 return holdPosStrategy.calPosMarginRatio(pos.getPosMargin(), pos.getFaceValue(), pos.getVolume(), posPnl, markPrice);
	}

	int ____________________________;
	
	private HoldPosStrategy getHoldPosStrategy(String asset, String symbol){
		Integer strategyId = this.genStrategyId(asset, symbol);
		StrategyBundle sb = strategyBundleMap.get(strategyId);
		return sb.getHoldPosStrategy();
	}
	
	private OrderStrategy getOrderStrategy(String asset, String symbol){
		Integer strategyId = this.genStrategyId(asset, symbol);
		StrategyBundle sb = strategyBundleMap.get(strategyId);
		return sb.getOrderStrategy();
	}
	
	private Integer genStrategyId(String asset, String symbol){
		PcContractVO pcContract = this.metadataService.getPcContract(asset, symbol);
		return pcContract.getContractType();
	}
	
	@Autowired(required=false)
	private void setBundleList(List<StrategyBundle> bundleList){
		for(StrategyBundle sb : bundleList){
			this.strategyBundleMap.put(sb.strategyId(), sb);
		}
	}
	
}
