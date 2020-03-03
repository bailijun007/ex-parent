/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum RmqTagEnum implements EnumDescribe {

    // BB_MATCH
    // book reset
    BB_BOOK_RESET(EventEnum.BB_BOOK_RESET.getCode(), EventEnum.BB_BOOK_RESET.getConstant()),

    // pending new
    BB_ORDER_PENDING_NEW(EventEnum.BB_ORDER_PENDING_NEW.getCode(), EventEnum.BB_ORDER_PENDING_NEW.getConstant()),

    // pending cancel
    BB_ORDER_PENDING_CANCEL(EventEnum.BB_ORDER_PENDING_CANCEL.getCode(), EventEnum.BB_ORDER_PENDING_CANCEL.getConstant()),

    //
    BB_MATCH_ORDER_MATCHED(EventEnum.BB_MATCH_ORDER_MATCHED.getCode(), EventEnum.BB_MATCH_ORDER_MATCHED.getConstant()),

    BB_MATCH_ORDER_NOT_MATCHED(EventEnum.BB_MATCH_ORDER_NOT_MATCHED.getCode(), EventEnum.BB_MATCH_ORDER_NOT_MATCHED.getConstant()),

    BB_MATCH_ORDER_CANCELLED(EventEnum.BB_MATCH_ORDER_CANCELLED.getCode(), EventEnum.BB_MATCH_ORDER_CANCELLED.getConstant()),

    BB_MATCH_ORDER_SNAPSHOT_CREATE(EventEnum.BB_MATCH_ORDER_SNAPSHOT_CREATE.getCode(), EventEnum.BB_MATCH_ORDER_SNAPSHOT_CREATE.getConstant()),

    BB_MATCH_CONSUMER_START(EventEnum.BB_MATCH_CONSUMER_START.getCode(), EventEnum.BB_MATCH_CONSUMER_START.getConstant()),
    BB_ORDER_REBASE(EventEnum.BB_ORDER_REBASE.getCode(), EventEnum.BB_ORDER_REBASE.getConstant()),
    //
    ;

    private int code;
    private String constant;

    RmqTagEnum(int code, String constant) {
        this.code = code;
        this.constant = constant;
    }

    public static Map<Integer, EventEnum> code2Enum = EnumSet.allOf(EventEnum.class).stream().collect(Collectors
            .toMap(vo -> vo.getCode(), Function.identity(), (oldValue, newValue) -> newValue));

    public int getCode() {
        return this.code;
    }

    public String getConstant() {
        return this.constant;
    }


}
