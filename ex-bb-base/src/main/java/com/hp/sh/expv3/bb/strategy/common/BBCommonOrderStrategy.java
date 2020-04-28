package com.hp.sh.expv3.bb.strategy.common;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.bb.component.FeeRatioService;
import com.hp.sh.expv3.bb.constant.OrderFlag;
import com.hp.sh.expv3.bb.constant.TradeRoles;
import com.hp.sh.expv3.bb.module.order.entity.BBOrder;
import com.hp.sh.expv3.bb.strategy.OrderStrategy;
import com.hp.sh.expv3.bb.strategy.data.OrderFeeParam;
import com.hp.sh.expv3.bb.strategy.data.OrderTrade;
import com.hp.sh.expv3.bb.strategy.vo.BBTradeVo;
import com.hp.sh.expv3.bb.strategy.vo.OrderFeeData;
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
public class BBCommonOrderStrategy implements OrderStrategy {


	@Autowired
	private FeeRatioService feeRatioService;

	/**
	 * 计算新订单费用
	 */
		//交易金额
	public OrderFeeData calcOrderAmt(OrderFeeParam orderParam){
		BigDecimal amount = calcAmount(orderParam.getVolume(), orderParam.getPrice());

		//开仓手续费
		BigDecimal fee = calcFee(amount, orderParam.getFeeRatio());

		//保证金
		BigDecimal orderMargin = calMargin(amount, orderParam.getMarginRatio());

		//总押金
		BigDecimal grossMargin = BigCalc.sum(fee, orderMargin);

		OrderFeeData orderAmount = new OrderFeeData();
		orderAmount.setAmount(amount);

		orderAmount.setOrderMargin(orderMargin);
		orderAmount.setFee(fee);
		orderAmount.setGrossMargin(grossMargin);

		return orderAmount;
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
	 * @return
	 */
	public TradeResult calcTradeResult(BBTradeVo tradeVo, BBOrder order){
		long userId = order.getUserId();
		String asset = order.getAsset();
		String symbol = order.getSymbol();
		int bidFlag = order.getBidFlag();

		TradeResult tradeResult = new TradeResult();

		tradeResult.setTradeVolume(tradeVo.getNumber());
		tradeResult.setTradePrice(tradeVo.getPrice());
		tradeResult.setTradeAmount(tradeVo.getPrice().multiply(tradeVo.getNumber()));
		tradeResult.setOrderCompleted(BigUtils.isZero(order.getVolume().subtract(order.getFilledVolume()).subtract(tradeVo.getNumber())));

		//手续费率
		BigDecimal tradeFeeRatio = order.getFeeRatio();
		if(TradeRoles.isMaker(tradeVo.getMakerFlag())){
			BigDecimal makerFeeRatio = feeRatioService.getMakerFeeRatio(userId, asset, symbol);
			if(BigUtils.lt(makerFeeRatio, tradeFeeRatio)){
				tradeFeeRatio = makerFeeRatio;
			}
		}
		tradeResult.setTradeFeeRatio(tradeFeeRatio);
		
		//手续费
		tradeResult.setTradeFee(tradeResult.getTradeAmount().multiply(tradeFeeRatio));

		//押金
		if(bidFlag==OrderFlag.BID_BUY){
			tradeResult.setTradeOrderMargin(tradeResult.getTradeAmount());
		}else{
			tradeResult.setTradeOrderMargin(tradeResult.getTradeVolume());
		}

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
		return new BigDecimal(fee.stripTrailingZeros().toPlainString());
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
