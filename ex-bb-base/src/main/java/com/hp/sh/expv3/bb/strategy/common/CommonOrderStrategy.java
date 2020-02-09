package com.hp.sh.expv3.bb.strategy.common;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.component.MetadataService;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.TradeRoles;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.msg.BBTradeMsg;
import com.hp.sh.expv3.bb.strategy.OrderStrategy;
import com.hp.sh.expv3.bb.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.bb.strategy.data.OrderTrade;
import com.hp.sh.expv3.bb.strategy.vo.OrderRatioData;
import com.hp.sh.expv3.bb.strategy.vo.TradeResult;
import com.hp.sh.expv3.utils.math.BigCalc;
import com.hp.sh.expv3.utils.math.BigUtils;
import com.hp.sh.expv3.utils.math.DecimalUtil;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * 
 * @author wangjg
 *
 */
@Component
public class CommonOrderStrategy implements OrderStrategy {

	
	@Autowired
	private FeeRatioService feeRatioService;
    
	/**
	 * 计算新订单费用
	 */
		//交易金额
	public OrderRatioData calcOrderAmt(OrderFeeParam orderParam){
		BigDecimal amount = calcAmount(orderParam.getVolume(), orderParam.getPrice());
		
		//基础货币价值
		BigDecimal baseValue = orderParam.getVolume();
		
		//开仓手续费
		BigDecimal openFee = calcFee(baseValue, orderParam.getFeeRatio());
		
		//平仓手续费
		BigDecimal closeFee = calcFee(baseValue, orderParam.getCloseFeeRatio());
		
		//保证金
		BigDecimal orderMargin = calMargin(amount, orderParam.getMarginRatio());
		
		//总押金
		BigDecimal grossMargin = BigCalc.sum(closeFee, openFee, orderMargin);

		OrderRatioData orderAmount = new OrderRatioData();
		orderAmount.setAmount(amount);
		orderAmount.setBaseValue(baseValue);
		
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setOpenFee(openFee);
		orderAmount.setCloseFee(closeFee);
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setGrossMargin(grossMargin);
		
		return orderAmount;
	}
	
	/**
	 * 按比例计算费用
	 * @param order 订单数据
	 * @param volume 分量
	 * @return
	 */
	public OrderRatioData calcRaitoAmt(BBOrder order, BigDecimal number){
		OrderRatioData orderAmount = new OrderRatioData();
		if(order.getBidFlag() == OrderFlag.BID_BUY){
			this.calcOpenRaitoAmt(order, number, orderAmount);
		}else{
			this.calcCloseRaitoAmt(order, number, orderAmount);
		}
		return orderAmount;
	}
	
	protected void calcCloseRaitoAmt(BBOrder order, BigDecimal number, OrderRatioData orderAmount) {
		orderAmount.setOpenFee(BigDecimal.ZERO);
		orderAmount.setCloseFee(BigDecimal.ZERO);
		orderAmount.setOrderMargin(BigDecimal.ZERO);
		orderAmount.setGrossMargin(BigDecimal.ZERO);
		
		BigDecimal amount = number.multiply(order.getFaceValue());

		orderAmount.setAmount(amount);
		orderAmount.setBaseValue(number);
	}

	/**
	 * 按比例计算开仓订单费用
	 * @param order
	 * @param number
	 * @return
	 */
	protected void calcOpenRaitoAmt(BBOrder order, BigDecimal number, OrderRatioData orderAmount){
		
		BigDecimal openFee; 
		BigDecimal closeFee; 
		BigDecimal orderMargin;
		BigDecimal grossMargin;

		if(BigUtils.eq(number, BigDecimal.ZERO)){
			openFee = BigDecimal.ZERO;
			closeFee = BigDecimal.ZERO;
			orderMargin = BigDecimal.ZERO;
			grossMargin = BigDecimal.ZERO;
		}else if(BigUtils.eq(number, order.getVolume().subtract(order.getFilledVolume()))){
			openFee = order.getFee();
			closeFee = BigDecimal.ZERO;
			orderMargin = order.getOrderMargin();
			grossMargin = order.getGrossMargin();
		}else{
			openFee = slope(number, order.getVolume(), order.getFee());
			closeFee = BigDecimal.ZERO;
			orderMargin = slope(number, order.getVolume(), order.getOrderMargin());
			grossMargin = BigCalc.sum(openFee, closeFee, orderMargin);
		}
		
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setOpenFee(openFee);
		orderAmount.setCloseFee(closeFee);
		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setGrossMargin(grossMargin);

		
		BigDecimal amount = number.multiply(order.getFaceValue());
		BigDecimal baseValue = number;

		orderAmount.setAmount(amount);
		orderAmount.setBaseValue(baseValue);
	}
	
	int ____________________________;

	public BigDecimal calcOrderMeanPrice(String asset, String symbol, List<? extends OrderTrade> tradeList){
		if(tradeList==null || tradeList.isEmpty()){
			return BigDecimal.ZERO;
		}
		BigDecimal amount = BigDecimal.ZERO;
		BigDecimal vols = BigDecimal.ZERO;
		for(OrderTrade trade : tradeList){
			amount = amount.add(calcAmount(trade.getVolume(), trade.getPrice()));
			vols = vols.add(trade.getVolume());
		}
		if(BigUtils.isZero(vols)){
			return BigDecimal.ZERO;
		}
		BigDecimal meanPrice = calcEntryPrice(vols, amount);
		return meanPrice;
	}
	
    /**
     * 计算成交均价
     *
     * @param isLong
     * @param baseValue
     * @param amt
     * @return
     */
    private static BigDecimal calcEntryPrice(BigDecimal volume, BigDecimal amt) {
    	return amt.divide(volume, Precision.COMMON_PRECISION, DecimalUtil.MORE).stripTrailingZeros();
    }

	int ___________________________;
	
	/**
	 * 计算本单的各项数据
	 * @param order
	 * @param tradeVo
	 * @param pcPosition
	 * @return
	 */
	public TradeResult calcTradeResult(BBTradeMsg tradeVo, BBOrder order){
		long userId = order.getUserId();
		String asset = order.getAsset();
		String symbol = order.getSymbol();
		int bidFlag = order.getBidFlag();
		
		TradeResult tradeResult = new TradeResult();
	
		tradeResult.setVolume(tradeVo.getNumber());
		tradeResult.setPrice(tradeVo.getPrice());
		tradeResult.setAmount(tradeVo.getPrice().multiply(tradeVo.getNumber()));
		tradeResult.setBaseValue(tradeVo.getNumber());
		tradeResult.setOrderCompleted(BigUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(tradeVo.getNumber())));
		
		//剩余数量
		tradeResult.setRemainVolume(BigCalc.subtract(order.getVolume(), order.getFilledVolume(), tradeResult.getVolume()));
		//押金
		if(bidFlag==OrderFlag.BID_BUY){
			tradeResult.setOrderMargin(tradeResult.getAmount());
			tradeResult.setRemainOrderMargin(order.getOrderMargin().subtract(tradeResult.getAmount()));
		}else{
			tradeResult.setOrderMargin(tradeResult.getVolume());
			tradeResult.setRemainOrderMargin(tradeResult.getRemainVolume());
		}
		
		//手续费&率
		tradeResult.setFeeRatio(order.getFeeRatio());
		BigDecimal fee = tradeResult.getAmount().multiply(tradeResult.getFeeRatio());
		tradeResult.setFee(fee);
		
		//maker fee
		if(tradeVo.getMakerFlag()==TradeRoles.MAKER){
			BigDecimal makerFeeRatio = feeRatioService.getMakerFeeRatio(userId, asset, symbol);
			tradeResult.setMakerFeeRatio(makerFeeRatio);
			BigDecimal makerFee = tradeResult.getAmount().multiply(makerFeeRatio);
			tradeResult.setMakerFee(makerFee);
		}
		
		/* **************** 仓位累计数据 **************** */
		
		return tradeResult;
	}

	int _____________________________;

	/**
	 * 按比例计算amount
	 * @param number 比例分子
	 * @param volume 比例分母
	 * @param amount 求值对象
	 * @return
	 */
	public static final BigDecimal slope(BigDecimal number, BigDecimal volume, BigDecimal amount){
		return number.multiply(amount).divide(volume, Precision.COMMON_PRECISION, Precision.LESS).stripTrailingZeros();
	}
	
	public static BigDecimal calcAmount(BigDecimal volume, BigDecimal price){
		return volume.multiply(price);
	}

	public static BigDecimal calcFee(BigDecimal amount, BigDecimal feeRatio){
		BigDecimal fee = amount.multiply(feeRatio);
		return fee.stripTrailingZeros();
	}
	
	/**
	 * 计算保证金
	 * @param baseValue 基础货币价值
	 * @param ratio
	 * @return
	 */
	public static final BigDecimal calMargin(BigDecimal amount, BigDecimal ratio){
		return amount.multiply(ratio);
	}
}