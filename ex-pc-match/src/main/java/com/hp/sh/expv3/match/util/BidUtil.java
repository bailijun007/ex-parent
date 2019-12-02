/**
 * @author zw
 * @date 2019/8/3
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.constant.CommonConst;

public final class BidUtil {

    public static String getPcBidCloseDesc(int bidFlag, int closeFlag) {
        return (CommonConst.YES == closeFlag)
                ? (isBid(bidFlag) ? "buy close short" : "sell close long")
                : (isBid(bidFlag) ? "buy open long" : "sell open short");
    }

    public static String getPcPosDesc(int longFlag) {
        return (CommonConst.YES == longFlag) ? "long" : "short";
    }

    public static String getSpotBidDesc(int bidFlag) {
        return isBid(bidFlag) ? "buy" : "sell";
    }

    public static String getPcBidCloseDesc(int bidFlag) {
        return isBid(bidFlag) ? "buy" : "sell";
    }

    public static boolean isBid(int bidFlag) {
        return CommonConst.YES == bidFlag;
    }

    public static boolean isLong(int longFlag) {
        return CommonConst.YES == longFlag;
    }

    public static boolean isClose(int closeFlag) {
        return CommonConst.YES == closeFlag;
    }

}
