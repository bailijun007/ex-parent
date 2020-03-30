/**
 * @author zw
 * @date 2019/8/21
 */
package com.hp.sh.expv3.match.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 事件类型，与操作类型无关，用于发消息
 */
public enum EventEnum implements EnumDescribe {
    /**
     * 深度事件
     */
    BB_BOOK(41, "BB_BOOK"),
    /**
     * 撮合事件
     */
    BB_MATCH(42, "BB_MATCH"),
    /**
     * 重置深度，发给撮合的消息
     */
    BB_BOOK_RESET(43, "BB_BOOK_RESET"),
    /**
     * 委托 pending new，发给撮合的消息，用于通知撮合将该委托撮合
     */
    BB_ORDER_PENDING_NEW(44, "BB_ORDER_PENDING_NEW"),
    /**
     * 委托 pending cancel，发给撮合的消息，用于通知撮合将该委托剩余未成交部分取消 并移除 bookOrder 列表
     */
    BB_ORDER_PENDING_CANCEL(45, "BB_ORDER_PENDING_CANCEL"),
    /**
     * 委托 匹配成功，由撮合发出，用于通知下游将 委托状态修改为 部分成交或者全部成交
     */
    BB_MATCH_ORDER_MATCHED(46, "BB_MATCH_ORDER_MATCHED"),
    /**
     * 委托 未匹配，由撮合发出，用于通知下游将 委托状态修改为 NEW
     */
    BB_MATCH_ORDER_NOT_MATCHED(47, "BB_MATCH_ORDER_NOT_MATCHED"),
    /**
     * 委托 在撮合中已取消，由撮合发出，用于通知下游将 委托状态修改为 CANCELED
     */
    BB_MATCH_ORDER_CANCELLED(48, "BB_MATCH_ORDER_CANCELLED"),
    /**
     * 撮合引擎中 book list 创建快照
     */
    BB_MATCH_ORDER_SNAPSHOT_CREATE(49, "BB_MATCH_ORDER_SNAPSHOT_CREATE"),
    /**
     * 撮合引擎启动
     */
    BB_MATCH_CONSUMER_START(50, "BB_MATCH_CONSUMER_START"),
    /**
     * rebase
     */
    BB_ORDER_REBASE(51, "BB_ORDER_REBASE"),
    // 成交 ，单用户级别
    BB_TRADE(52, "BB_TRADE"),;

    private int code;
    private String constant;

    EventEnum(int code, String constant) {
        this.code = code;
        this.constant = constant;
    }

    public static Map<Integer, EventEnum> code2Enum = EnumSet.allOf(EventEnum.class).stream().collect(Collectors
            .toMap(vo -> vo.getCode(), Function.identity(), (oldValue, newValue) -> newValue));

    @Override
    public int getCode() {
        return this.code;
    }

    @Override
    public String getConstant() {
        return this.constant;
    }

}
