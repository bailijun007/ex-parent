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

    BOOK_RESET(EventEnum.BOOK_RESET.getCode(), EventEnum.BOOK_RESET.getConstant()),

    ORDER_PENDING_NEW(EventEnum.ORDER_PENDING_NEW.getCode(), EventEnum.ORDER_PENDING_NEW.getConstant()),

    ORDER_PENDING_CANCEL(EventEnum.ORDER_PENDING_CANCEL.getCode(), EventEnum.ORDER_PENDING_CANCEL.getConstant()),

    TRADE(EventEnum.TRADE.getCode(), EventEnum.TRADE.getConstant()),

    MATCH_ORDER_MATCHED(EventEnum.MATCH_ORDER_MATCHED.getCode(), EventEnum.MATCH_ORDER_MATCHED.getConstant()),

    MATCH_ORDER_NOT_MATCHED(EventEnum.MATCH_ORDER_NOT_MATCHED.getCode(), EventEnum.MATCH_ORDER_NOT_MATCHED.getConstant()),

    MATCH_ORDER_CANCELLED(EventEnum.MATCH_ORDER_CANCELLED.getCode(), EventEnum.MATCH_ORDER_CANCELLED.getConstant()),

    MATCH_ORDER_SNAPSHOT_CREATE(EventEnum.MATCH_ORDER_SNAPSHOT_CREATE.getCode(), EventEnum.MATCH_ORDER_SNAPSHOT_CREATE.getConstant()),

    MATCH_CONSUMER_START(EventEnum.MATCH_CONSUMER_START.getCode(), EventEnum.MATCH_CONSUMER_START.getConstant()),
    ORDER_REBASE(-1, "ORDER_REBASE"),
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
