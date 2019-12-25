/**
 * @author corleone
 * @date 2018/9/14 0014
 */
package com.hp.sh.expv3.match.constant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public final class CommonConst {

    public static final int NO = 0;
    public static final int YES = 1;

    public static final String ASSET_SYMBOL_CONNECTOR = "__";

    public static final long INVALID_ID = -1L;
    public static final int COMMON_PRECISION = 18;
    public static final BigDecimal ERROR_THRESHOLD = BigDecimal.ONE.subtract(BigDecimal.TEN.pow(COMMON_PRECISION));
    public static final int PERCENT_PRECISION = 4;
    public static final int LEVERAGE_PRECISION = 2;

    public static final String NO_STRING = "0";
    public static final String YES_STRING = "1";

    public static final int BID = YES;
    public static final int ASK = NO;

    public static final int CLOSE = YES;
    public static final int OPEN = NO;

    public static final int LONG = YES;
    public static final int SHORT = NO;

    public static final int MAKER = YES;
    public static final int TAKER = NO;

    public static final int BID_OPPOSITE = ASK;
    public static final int ASK_OPPOSITE = BID;

    public static final int LONG_OPPOSITE = SHORT;
    public static final int SHORT_OPPOSITE = LONG;

    public static final int CLOSE_OPPOSITE = NO;
    public static final int OPEN_OPPOSITE = YES;

    public static final int ACCOUNT_TRANSFER_TARGET_RECEIVE_FLAG_YES = YES;
    public static final int ACCOUNT_TRANSFER_TARGET_RECEIVE_FLAG_NO = NO;

    public static final int ORDER_VISIABLE_FLAG_YES = YES;
    public static final int ORDER_VISIABLE_FLAG_NO = NO;

    public static final int ORDER_TRIGGER_FLAG_YES = YES;
    public static final int ORDER_TRIGGER_FLAG_NO = NO;

    public static final boolean isYes(Integer flag) {
        return (null == flag) ? false : (YES == flag.intValue());
    }

    public static final Set<Integer> YorN = new HashSet<Integer>() {{
        add(NO);
        add(YES);
    }};

    public static final RoundingMode FOUR_DOWN_FIVE_UP = RoundingMode.HALF_UP;

}