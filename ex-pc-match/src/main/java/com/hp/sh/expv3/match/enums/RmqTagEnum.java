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

    // PC_MATCH
    PC_BOOK_RESET(EventEnum.PC_BOOK_RESET.getCode(), EventEnum.PC_BOOK_RESET.getConstant()),

    PC_ORDER_PENDING_NEW(EventEnum.PC_ORDER_PENDING_NEW.getCode(), EventEnum.PC_ORDER_PENDING_NEW.getConstant()),

    PC_ORDER_PENDING_CANCEL(EventEnum.PC_ORDER_PENDING_CANCEL.getCode(), EventEnum.PC_ORDER_PENDING_CANCEL.getConstant()),

    PC_TRADE(EventEnum.PC_TRADE.getCode(), EventEnum.PC_TRADE.getConstant()),

    PC_MATCH_ORDER_MATCHED(EventEnum.PC_MATCH_ORDER_MATCHED.getCode(), EventEnum.PC_MATCH_ORDER_MATCHED.getConstant()),

    PC_MATCH_ORDER_NOT_MATCHED(EventEnum.PC_MATCH_ORDER_NOT_MATCHED.getCode(), EventEnum.PC_MATCH_ORDER_NOT_MATCHED.getConstant()),

    PC_MATCH_ORDER_CANCELLED(EventEnum.PC_MATCH_ORDER_CANCELLED.getCode(), EventEnum.PC_MATCH_ORDER_CANCELLED.getConstant()),

    PC_MATCH_ORDER_SNAPSHOT_CREATE(EventEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getCode(), EventEnum.PC_MATCH_ORDER_SNAPSHOT_CREATE.getConstant()),

    // 此消息一定要和撮合消息 在一个 queue 里，确保顺序性
    PC_POS_LIQ_LOCKED(EventEnum.PC_POS_LIQ_LOCKED.getCode(), EventEnum.PC_POS_LIQ_LOCKED.getConstant()),

    PC_MATCH_SAME_SIDE_CLOSE_ORDER_CANCELLED(EventEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_CANCELLED.getCode(), EventEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_CANCELLED.getConstant()),

    PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED(EventEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED.getCode(), EventEnum.PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED.getConstant()),

    PC_MATCH_CONSUMER_START(EventEnum.PC_MATCH_CONSUMER_START.getCode(), EventEnum.PC_MATCH_CONSUMER_START.getConstant()),
    PC_ORDER_REBASE(EventEnum.PC_ORDER_REBASE.getCode(), EventEnum.PC_ORDER_REBASE.getConstant()),
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
