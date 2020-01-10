/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.constant;

public final class MatchTaskConst {

    public static final int PRIORITY_ORDER_INIT = 0;
    public static final int PRIORITY_ORDER_BOOK_RESET = 5;
    public static final int PRIORITY_ORDER_PENDING_NEW = 10;
    /**
     * can and new 必须一致，用于定序
     */
    public static final int PRIORITY_ORDER_PENDING_CANCEL = PRIORITY_ORDER_PENDING_NEW;
    public static final int PRIORITY_ORDER_CANCEL_BY_LIQ = PRIORITY_ORDER_PENDING_NEW;

}