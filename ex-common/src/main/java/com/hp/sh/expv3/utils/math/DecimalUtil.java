/**
 * @author corleone
 * @date 2018/7/6 0006
 */
package com.hp.sh.expv3.utils.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class DecimalUtil {

    /**
     * 取消小数点后多余的0
     * 如 1.2000 -> 1.2
     *
     * @param input
     * @return
     */
    public static final BigDecimal trimZero(BigDecimal input) {
        if (null == input) {
            return input;
        } else {
            return input.stripTrailingZeros();
        }
    }

    /**
     * 不启用科学计数法
     *
     * @param input
     * @return
     */
    public static final String literal(BigDecimal input) {
        if (null == input) {
            return null;
        } else {
            return input.toPlainString();
        }
    }

    /**
     * 取消小数点后多余的并不启用科学计数法
     *
     * @param input
     * @return
     */
    public static final String toTrimLiteral(BigDecimal input) {
        if (null == input) {
            return "";
        } else {
            return input.stripTrailingZeros().toPlainString();
        }
    }

    /**
     * 按照买卖 设置精度
     * depth 中 精度变化将使用到
     *
     * @param price
     * @param precision
     * @param isBid
     * @return
     */
    public static BigDecimal buildPrice(BigDecimal price, int precision, boolean isBid) {
        return new BigDecimal(price.stripTrailingZeros().toPlainString())
                .setScale(precision, isBid ? LESS : MORE);
    }

    public static final RoundingMode MORE = RoundingMode.UP;
    public static final RoundingMode LESS = RoundingMode.DOWN;

    /**
     * 简单的四舍五入
     *
     * @param price
     * @param precision
     * @return
     */
    public static BigDecimal convertPriceScale(BigDecimal price, int precision) {
        return new BigDecimal(price.toPlainString()).setScale(precision, RoundingMode.HALF_UP);
    }

    public static BigDecimal intDivide(BigDecimal dividend, BigDecimal divisor, RoundingMode mode) {
        return dividend.divide(divisor, 0, mode);
    }

    public static BigDecimal intDivide(BigDecimal dividend, BigDecimal divisor) {
        return dividend.divide(divisor, 0, LESS);
    }

    public static BigDecimal intScale(BigDecimal number, RoundingMode mode) {
        return number.setScale(0, mode);
    }

    public static void main(String[] args) {
    	BigDecimal b1 = new BigDecimal(0.1);
    	BigDecimal b2 = new BigDecimal(0.5);
    	System.out.println("b1="+b1+"\nb2="+b2+",c="+(b1.add(b2)));
	}
}