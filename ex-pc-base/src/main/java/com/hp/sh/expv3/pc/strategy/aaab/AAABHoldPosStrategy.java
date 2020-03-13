package com.hp.sh.expv3.pc.strategy.aaab;

import java.math.BigDecimal;

import com.hp.sh.expv3.pc.constant.OrderFlag;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.utils.IntBool;
import com.hp.sh.expv3.utils.math.DecimalUtil;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * AAAB持仓计算策略
 * @author wangjg
 *
 */
public class AAABHoldPosStrategy implements HoldPosStrategy{
	
	/**
	 * 计算仓位保证金率
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数*最新标记价格）
	 * @return
	 */
	@Override
	public BigDecimal calPosMarginRatio(BigDecimal posMargin, BigDecimal posPnl, BigDecimal faceValue, BigDecimal volume, BigDecimal markPrice){
		BigDecimal marginRatio = posMargin.add(posPnl).divide(faceValue.multiply(volume).multiply(markPrice), Precision.COMMON_PRECISION, DecimalUtil.LESS);
		return marginRatio;
	}

	/**
	 * 简化。。。
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
	    BigDecimal pnl = this.calcPnl(longFlag, volume, faceValue, meanPrice, markPrice);
	    BigDecimal amount = AAABCompFieldCalc.calcAmount(volume, faceValue, meanPrice);
	    return amount.multiply(initMarginRatio).subtract(pnl.min(BigDecimal.ZERO)).stripTrailingZeros();//保证金覆盖亏损
	}

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
		BigDecimal priceDiff;
		if(longFlag==OrderFlag.TYPE_LONG){
			priceDiff = closePrice.subtract(openPrice);
		}else{
			priceDiff = openPrice.subtract(closePrice);
		}
		BigDecimal count = volume.multiply(faceValue);
		BigDecimal pnl = priceDiff.multiply(count);
		return pnl;
    }
	
	/**
	 * 成交均价
	 * @param isLong
	 * @param baseValue 几个币
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
	 * 计算仓位的强平价(预估强平价)
	 * 保证金率 =（保证金+未实现盈亏) / (合约面值*持仓张数*最新标记价格）
	 * 保证金率 =（保证金+(cp-op)*f*v) / (f*v*cp）
	 * long : 
	 * mr = (m + (cp - op) * fv) / (fv * cp)
	 * mr * fv * cp = m + (cp - op) * fv
	 * mr * fv * cp = m + cp * fv - op * fv
	 * mr * fv * cp - cp * fv = m - op * fv
	 * cp * (mr * fv - fv) = m - op * fv
	 * cp * fv * (mr - 1) = m - op * fv
	 * cp = (m - op * fv) / (fv * (mr - 1))
	 * short : 
	 * mr = (m + (op - cp) * fv) / (fv * cp)
	 * mr * (fv * cp) = m + op*fv - cp * fv
	 * mr * fv * cp = m + op*fv - cp * fv
	 * mr * fv * cp + cp * fv = m + op*fv
	 * cp * fv * (mr + 1) = m + op*fv
	 * cp = (m + op*fv) / (fv * (mr + 1)) 
	 * cp = (m/fv + op) / (mr + 1)
	 */
	public BigDecimal calcLiqPrice(int longFlag, BigDecimal faceValue, BigDecimal volume, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		BigDecimal fv = faceValue.multiply(volume);
		if(longFlag==OrderFlag.TYPE_LONG){
			return posMargin.subtract(openPrice.multiply(fv)).divide(fv.multiply(holdMarginRatio.subtract(BigDecimal.ONE)));
		}else{
			return posMargin.divide(fv).add(openPrice).divideToIntegralValue(holdMarginRatio.add(BigDecimal.ONE));
		}
	}
	
	/**
	 * long:
	 * pnl = (cp-op)*fv
	 * (cp-op) = pnl/fv
	 * cp = pnl/fv + op
	 * 
	 * short:
	 * pnl = (op-cp)*fv
	 * op-cp = pnl/fv
	 * cp = op - pnl/fv
	 * 
	 * @return
	 */
	public BigDecimal calcLiqPrice3(int longFlag, BigDecimal faceValue, BigDecimal volume, BigDecimal openPrice, BigDecimal holdMarginRatio, BigDecimal posMargin){
		BigDecimal fv = faceValue.multiply(volume);
		BigDecimal holdMargin = fv.multiply(openPrice).multiply(holdMarginRatio);
		BigDecimal pnl = posMargin.subtract(holdMargin);
		pnl = pnl.negate();
		if(IntBool.isTrue(longFlag)){ 
			BigDecimal liqPrice = pnl.divide(fv, Precision.COMMON_PRECISION, Precision.LESS).add(openPrice);
			return liqPrice;
		}else{
			BigDecimal liqPrice = openPrice.subtract(pnl.divide(fv, Precision.COMMON_PRECISION, Precision.LESS));
			return liqPrice;
		}
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
	public BigDecimal calcBankruptPrice(Integer longFlag, BigDecimal volume, BigDecimal faceValue, BigDecimal posMargin, BigDecimal openPrice) {
		BigDecimal holdMarginRatio = BigDecimal.ONE;
		BigDecimal fv = faceValue.multiply(volume);
		BigDecimal holdMargin = fv.multiply(openPrice).multiply(holdMarginRatio);
		BigDecimal pnl = posMargin.subtract(holdMargin);
		pnl = pnl.negate();
		if(IntBool.isTrue(longFlag)){
			BigDecimal liqPrice = pnl.divide(fv, Precision.COMMON_PRECISION, Precision.LESS).add(openPrice);
			return liqPrice;
		}else{
			BigDecimal liqPrice = openPrice.subtract(pnl.divide(fv, Precision.COMMON_PRECISION, Precision.LESS));
			return liqPrice;
		}
	}

}
