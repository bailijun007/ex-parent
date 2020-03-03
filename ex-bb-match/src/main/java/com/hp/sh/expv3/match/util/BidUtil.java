/**
 * @author zw
 * @date 2019/8/3
 */
package com.hp.sh.expv3.match.util;

import com.hp.sh.expv3.match.constant.CommonConst;

public final class BidUtil {

    public static String getBidDesc(int bidFlag) {
        return isBid(bidFlag) ? "buy" : "sell";
    }

    public static boolean isBid(int bidFlag) {
        return CommonConst.YES == bidFlag;
    }

}
