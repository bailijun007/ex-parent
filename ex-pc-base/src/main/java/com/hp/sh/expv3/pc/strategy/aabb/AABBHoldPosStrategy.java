package com.hp.sh.expv3.pc.strategy.aabb;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.strategy.data.PosData;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.DecimalUtil;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * AABB持仓计算策略
 * @author wangjg
 *
 */
public class AABBHoldPosStrategy implements HoldPosStrategy{
	
	/**
	 * 计算收益
	 * @param faceValue 面值
	 * @param volume 张数
	 * @param openPrice 开仓价
	 * @param closePrice 平仓价
	 * @return
	 */
	public BigDecimal calcPnl(int longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal openPrice, BigDecimal closePrice){
		BigDecimal amount = faceValue.multiply(volume); 	//金额（USD）
		BigDecimal openBase = amount.divide(openPrice, Precision.COMMON_PRECISION ,Precision.LESS).stripTrailingZeros(); 	//基础货币（BTC）
		BigDecimal closeBase = amount.divide(closePrice, Precision.COMMON_PRECISION ,Precision.LESS).stripTrailingZeros(); 	//基础货币（BTC）
		BigDecimal pnl = closeBase.subtract(openBase) ;
		if(longFlag==OrderFlag.TYPE_LONG){
			return pnl.negate();
		}else{
			return pnl;
		}
	}
	
	/**
	 * 成交均价
	 * @param isLong
	 * @param baseValue
	 * @param amt
	 * @return
	 */
	@Override
    public BigDecimal calcMeanPrice(int longFlag, BigDecimal baseValue, BigDecimal amount) {
        if (IntBool.isTrue(longFlag)) {
            return amount.divide(baseValue, Precision.COMMON_PRECISION, DecimalUtil.MORE).stripTrailingZeros();
        } else {
            return amount.divide(baseValue, Precision.COMMON_PRECISION, DecimalUtil.LESS).stripTrailingZeros();
        }
    }

	/**
	 * 计算仓位的强平价(预估强平价)
	 * @param longFlag 多/空
	 * @param amount 持仓金额
	 * @param meanPrice 均价 
	 * @param holdMarginRatio 维持保证金率
	 * @param posMargin 保证金
	 * @return 强平价
	 */
	public BigDecimal calcLiqPrice(int longFlag, BigDecimal faceValue, BigDecimal volume, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		BigDecimal amount = faceValue.multiply(volume);
		return PcPriceCalc.calcLiqPrice( holdMarginRatio, IntBool.isTrue(longFlag), openPrice, amount, posMargin, Precision.COMMON_PRECISION );
	}
	
	/**
	 * 计算仓位保证金率
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数／最新标记价格）
	 * @return
	 */
	@Override
	public BigDecimal calPosMarginRatio(BigDecimal posMargin, BigDecimal faceValue, BigDecimal volume, BigDecimal posPnl, BigDecimal markPrice){
//		BigDecimal marginRatio = posMargin.add(posPnl).divide((faceValue.multiply(volume).divide(markPrice)));
		BigDecimal marginRatio = posMargin.add(posPnl).multiply(markPrice).divide(faceValue.multiply(volume), Precision.COMMON_PRECISION, DecimalUtil.LESS);
		
		return marginRatio;
	}

	/**
	 * 用 均价 标记价格 未实现盈亏 计算 仓位保证金
	 */
	@Override
	public BigDecimal calcInitMargin(Integer longFlag, BigDecimal initMarginRatio, BigDecimal volume, BigDecimal faceValue, BigDecimal meanPrice, BigDecimal markPrice) {
        // ( 1 / leverage ) * volume = volume / leverage
		BigDecimal amount = AABBCompFieldCalc.calcAmount(volume, faceValue);
        BigDecimal pnl = PnlCalc.calcPnl(longFlag, amount, meanPrice, markPrice);
        BigDecimal baseValue = AABBCompFieldCalc.calcBaseValue(amount, meanPrice);
        return baseValue.multiply(initMarginRatio).subtract(pnl.min(BigDecimal.ZERO)).stripTrailingZeros();
	}
	
	/**
	 * 计算破产价
	 * @param isLong
	 * @param openPrice
	 * @param amt
	 * @param margin
	 * @return
	 */
	@Override
	public BigDecimal calcBankruptPrice(Integer longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal margin, BigDecimal openPrice) {
		BigDecimal _amount = AABBCompFieldCalc.calcAmount(faceValue, volume);
		return PcPriceCalc.calcBankruptPrice(IntBool.isTrue(longFlag), openPrice, _amount, margin, Precision.COMMON_PRECISION );
	}
	
}
