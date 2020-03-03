package com.hp.sh.expv3.pc.strategy.aaab;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.strategy.aabb.AABBCompFieldCalc;
import com.hp.sh.expv3.pc.strategy.data.PosData;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.DecimalUtil;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * AABB持仓计算策略
 * @author wangjg
 *
 */
public class AAABHoldPosStrategy implements HoldPosStrategy{
	
	/**
	 * 计算收益
	 * @param longFlag 是否多仓
	 * @param amt 成交金额
	 * @param openPrice 均价
	 * @param closePrice 平仓价
	 * @return
	 */
	@Override
	public BigDecimal calcPnl(int longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal openPrice, BigDecimal closePrice) {
		BigDecimal priceDiff = closePrice.subtract(openPrice);
		BigDecimal count = volume.multiply(faceValue);
		
		BigDecimal pnl = priceDiff.multiply(count);
		if(longFlag==OrderFlag.TYPE_LONG){
			return pnl;
		}else{
			return pnl.negate();
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
    public BigDecimal calcMeanPrice(int longFlag, BigDecimal baseValue, BigDecimal amt) {
        if (IntBool.isTrue(longFlag)) {
            return amt.divide(baseValue, Precision.COMMON_PRECISION, DecimalUtil.MORE).stripTrailingZeros();
        } else {
            return amt.divide(baseValue, Precision.COMMON_PRECISION, DecimalUtil.LESS).stripTrailingZeros();
        }
    }
	
	/**
	 * TODO
	 */
	@Override
	public BigDecimal calcLiqPrice(PosData pos) {
		BigDecimal amount = AAABCompFieldCalc.calcAmount(pos.getVolume(), pos.getFaceValue(), pos.getMeanPrice());
		return PcPriceCalc.calcLiqPrice( pos.getHoldMarginRatio(), IntBool.isTrue(pos.getLongFlag()), pos.getMeanPrice(), amount, pos.getPosMargin(), Precision.COMMON_PRECISION );
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
	@Override
	public BigDecimal calcLiqPrice(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		return PcPriceCalc.calcLiqPrice( holdMarginRatio, IntBool.isTrue(longFlag), openPrice, amount, posMargin, Precision.COMMON_PRECISION );
	}
	
	/**
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数／最新标记价格）
	 * 保证金率 =（保证金+(cp-op)*fv) / (合约面值*持仓张数／cp）
	 * mr =（m+(cp-op)*fv) / (fv／cp）
	 * mr =（m+(cp-op)*fv) * (cp/fv）
	 * mr =（m+(cp-op)*fv) * cp / fv
	 * mr =（m+ (cp-op)*fv) / fv * cp
	 * mr =（m+ (cp-op)*fv)* cp / fv 
	 * 
	 * （m + (cp-op)*fv)* cp = mr*fv
	 * （m + (cp-op)*fv)* cp = mr*fv
	 * 
	 */
	public BigDecimal calcLiqPrice2(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		return null;
	}
	
	/**
	 * pnl = (cp-op)*fv
	 * (cp-op) = pnl/fv
	 * cp = pnl/fv + op
	 * 
	 * @return
	 */
	public BigDecimal calcLiqPrice3(int longFlag, BigDecimal amount, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		if(IntBool.isTrue(longFlag)){
			BigDecimal holdMargin = amount.multiply(openPrice).multiply(holdMarginRatio);
			BigDecimal pnl = posMargin.subtract(holdMargin);
			BigDecimal liqPrice = pnl.divide(amount, Precision.COMMON_PRECISION, Precision.LESS).subtract(openPrice);
			return liqPrice;
		}else{
			BigDecimal holdMargin = amount.multiply(openPrice).multiply(holdMarginRatio);
			BigDecimal pnl = posMargin.subtract(holdMargin);
			BigDecimal liqPrice = pnl.divide(amount, Precision.COMMON_PRECISION, Precision.LESS).add(openPrice);
			return liqPrice;
		}
	}
	
	/**
	 * 计算仓位保证金率
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数／最新标记价格）
	 * @return
	 */
	@Override
	public BigDecimal calPosMarginRatio(BigDecimal posMargin, BigDecimal posPnl, BigDecimal faceValue, BigDecimal volume, BigDecimal markPrice){
		BigDecimal marginRatio = posMargin.add(posPnl).multiply(markPrice).divide(faceValue.multiply(volume), Precision.COMMON_PRECISION, DecimalUtil.LESS);
		
		return marginRatio;
	}

	/**
	 * 用 均价 标记价格 未实现盈亏 计算 仓位保证金
	 * @param longFlag
	 * @param initMarginRatio
	 * @param amount
	 * @param meanPrice
	 * @param markPrice
	 * @return
	 */
	@Override
	public BigDecimal calcInitMargin(Integer longFlag, BigDecimal initMarginRatio, BigDecimal volume, BigDecimal faceValue, BigDecimal meanPrice, BigDecimal markPrice) {
        // ( 1 / leverage ) * volume = volume / leverage
        BigDecimal pnl = this.calcPnl(longFlag, volume, faceValue, meanPrice, markPrice);
        BigDecimal baseValue = AABBCompFieldCalc.calcBaseValue(volume, faceValue, meanPrice);
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
	
	public BigDecimal calcBankruptPrice3(Integer longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal margin, BigDecimal openPrice) {
		if(IntBool.isTrue(longFlag)){
			BigDecimal amount = faceValue.multiply(volume);
			BigDecimal pnl = margin;
			BigDecimal liqPrice = pnl.divide(amount, Precision.COMMON_PRECISION, Precision.LESS).subtract(openPrice);
			return liqPrice;
		}else{
			BigDecimal amount = faceValue.multiply(volume);
			BigDecimal pnl = margin;
			BigDecimal liqPrice = pnl.divide(amount, Precision.COMMON_PRECISION, Precision.LESS).add(openPrice);
			return liqPrice;
		}
	}
}
