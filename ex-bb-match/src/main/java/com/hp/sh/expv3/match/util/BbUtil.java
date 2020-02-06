/**
 * @author zw
 * @date 2019/8/3
 */
package com.hp.sh.expv3.match.util;

import com.google.common.collect.ImmutableMap;
import com.hp.sh.expv3.match.constant.CommonConst;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

import static com.hp.sh.expv3.match.constant.CommonConst.*;


/**
 * B工具类
 */
public final class BbUtil {

    public static final BigDecimal calcUnfilledNumber(BigDecimal number, BigDecimal filledNumber) {
        return number.subtract(filledNumber);
    }

    public static final BigDecimal calcBookNumber(BigDecimal number, BigDecimal filledNumber, BigDecimal displayNumber) {
        BigDecimal unfilledNumber = calcUnfilledNumber(number, filledNumber);
        return unfilledNumber.min(displayNumber).stripTrailingZeros();
    }

    public static final boolean isLiq(boolean isLong, BigDecimal liqPrice, BigDecimal markPrice) {
        if (isLong) {
            return markPrice.compareTo(liqPrice) <= 0;
        } else {
            return markPrice.compareTo(liqPrice) >= 0;
        }
    }

    public static final int oppositeBidFlag(int bidFlag) {
        switch (bidFlag) {
            case BID:
                return BID_OPPOSITE;
            case ASK:
                return ASK_OPPOSITE;
            default:
                throw new RuntimeException("impossible");
        }
    }

    public static final boolean isMaker(int makerFlag) {
        return YES == makerFlag;
    }

    public static final boolean isClose(int closeFlag) {
        return YES == closeFlag;
    }

    public static final boolean isBid(int bidFlag) {
        return YES == bidFlag;
    }

    public static final boolean isLong(int longFlag) {
        return YES == longFlag;
    }

    public static final String concatAssetAndSymbol(String pattern, String asset, String symbol) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = "${asset}" + CommonConst.ASSET_SYMBOL_CONNECTOR + "${symbol}";
        }
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("asset", asset, "symbol", symbol));
    }

    public static final Tuple2<String, String> splitAssetAndSymbol(String assetSymbol) {
        return splitAssetAndSymbol(assetSymbol, CommonConst.ASSET_SYMBOL_CONNECTOR);
    }

    public static final Tuple2<String, String> splitAssetAndSymbol(String assetSymbol, String spliter) {
        String[] assetSymbols = assetSymbol.split(CommonConst.ASSET_SYMBOL_CONNECTOR);
        return new Tuple2<>(assetSymbols[0], assetSymbols[1]);
    }

    public static final String concatAccountIdAndAssetAndSymbol(String pattern, long accountId, String asset, String symbol) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = "${accountId}_${asset}" + CommonConst.ASSET_SYMBOL_CONNECTOR + "${symbol}";
        }
        return StringReplaceUtil.replace(pattern, ImmutableMap.of("accountId", "" + accountId, "asset", asset, "symbol", symbol));
    }

    public static final int getLongFlag(int closeFlag, int bidFlag) {
        if (isYes(closeFlag)) {
            return isYes(bidFlag) ? SHORT : LONG;
        } else {
            return isYes(bidFlag) ? LONG : SHORT;
        }
    }

    public static final int getBidFlag(int closeFlag, int longFlag) {
        if (isYes(closeFlag)) {
            return isYes(longFlag) ? ASK : BID;
        } else {
            return isYes(longFlag) ? BID : ASK;
        }
    }

    public static String getSpotBidDesc(int bidFlag) {
        return isBid(bidFlag) ? "buy" : "sell";
    }

}