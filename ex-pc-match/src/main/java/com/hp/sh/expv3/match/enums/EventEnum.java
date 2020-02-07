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
    PC_BOOK(1, "PC_BOOK"),
    /**
     * 成交事件
     */
    PC_TRADE(2, "PC_TRADE"),
    /**
     * 永续合约账户更新
     */
    PC_ACCOUNT_UPDATE(3, "PC_ACCOUNT_UPDATE"),
    /**
     * 创建委托事件
     */
    PC_ORDER_CREATE(4, "PC_ORDER_CREATE"),
    /**
     * 更新委托事件
     */
    PC_ORDER_UPDATE(5, "PC_ORDER_UPDATE"),
    /**
     * 取消委托事件
     */
    PC_ORDER_CANCEL(6, "PC_ORDER_CANCEL"),
    /**
     * 完成委托事件
     */
    PC_ORDER_COMPLETED(7, "PC_ORDER_COMPLETED"),
    /**
     * 账户变更事件
     */
    ACCOUNT_UPDATE(8, "FUND_ACCOUNT_UPDATE"),
    /**
     * 资金账户变更
     */
    FUND_ACCOUNT_UPDATE(9, "FUND_ACCOUNT_UPDATE"),
    /**
     * 委托失败
     */
    PC_ORDER_FAILED(10, "PC_ORDER_FAILED"),
    /**
     * 建仓
     */
    PC_POS_CREATE(11, "PC_POS_CREATE"),
    /**
     * 仓位变动
     */
    PC_POS_CHANGE(12, "PC_POS_CHANGE"),
    /**
     * 平仓
     */
    PC_POS_CLOSE(13, "PC_POS_CLOSE"),
    /**
     * 仓位强平
     */
    PC_POS_FORCE_CLOSE(14, "PC_POS_FORCE_CLOSE"),
    /**
     * 新增提币申请
     */
    FUND_ACCOUNT_WITHDRAW_CREATE(15, "FUND_ACCOUNT_WITHDRAW_CREATE"),
    /**
     * 提币申请不通过
     */
    FUND_ACCOUNT_WITHDRAW_AUDIT_FAIL(16, "FUND_ACCOUNT_WITHDRAW_AUDIT_FAIL"),
    /**
     * 提币申请通过
     */
    FUND_ACCOUNT_WITHDRAW_AUDIT_PASS(17, "FUND_ACCOUNT_WITHDRAW_AUDIT_PASS"),
    /**
     * 仓位保证金更新
     */
    PC_POS_MARGIN_CHANGE(18, "PC_POS_MARGIN_CHANGE"),
    /**
     * 仓位杠杆更新
     */
    PC_POS_LEVERAGE_CHANGE(19, "PC_POS_LEVERAGE_CHANGE"),
    /**
     * 新增充币订单
     */
    FUND_ACCOUNT_DEPOSIT_CREATE(20, "FUND_ACCOUNT_DEPOSIT_CREATE"),
    /**
     * 新增充币到账
     */
    FUND_ACCOUNT_DEPOSIT_CONFIRM(21, "FUND_ACCOUNT_DEPOSIT_CONFIRM"),
    /**
     * 新增充币失败
     */
    FUND_ACCOUNT_DEPOSIT_FAIL(22, "FUND_ACCOUNT_DEPOSIT_FAIL"),
    /**
     * 提币 链上 pending
     */
    FUND_ACCOUNT_WITHDRAW_CHAIN_PENDING(23, "FUND_ACCOUNT_WITHDRAW_CHAIN_PENDING"),
    /**
     * 重置深度，发给撮合的消息
     */
    PC_BOOK_RESET(24, "PC_BOOK_RESET"),
    /**
     * 委托 pending new，发给撮合的消息，用于通知撮合将该委托撮合
     */
    PC_ORDER_PENDING_NEW(25, "PC_ORDER_PENDING_NEW"),
    /**
     * 委托 pending cancel，发给撮合的消息，用于通知撮合将该委托剩余未成交部分取消 并移除 bookOrder 列表
     */
    PC_ORDER_PENDING_CANCEL(26, "PC_ORDER_PENDING_CANCEL"),
    /**
     * 委托 匹配成功，由撮合发出，用于通知下游将 委托状态修改为 部分成交或者全部成交
     */
    PC_MATCH_ORDER_MATCHED(27, "PC_MATCH_ORDER_MATCHED"),
    /**
     * 委托 未匹配，由撮合发出，用于通知下游将 委托状态修改为 NEW
     */
    PC_MATCH_ORDER_NOT_MATCHED(28, "PC_MATCH_ORDER_NOT_MATCHED"),
    /**
     * 委托 在撮合中已取消，由撮合发出，用于通知下游将 委托状态修改为 CANCELED
     */
    PC_MATCH_ORDER_CANCELLED(29, "PC_MATCH_ORDER_CANCELLED"),
    /**
     * 撮合引擎中 book list 创建快照
     */
    PC_MATCH_ORDER_SNAPSHOT_CREATE(30, "PC_MATCH_ORDER_SNAPSHOT_CREATE"),
    /**
     * 仓位被冻结
     */
    PC_POS_LIQ_LOCKED(32, "PC_POS_LIQ_LOCKED"),
    /**
     * 撮合引擎启动
     */
    PC_MATCH_CONSUMER_START(37, "PC_MATCH_CONSUMER_START"),
    /**
     * 仓位维持保证金不足，待强平仓位同向 平仓委托 已取消，margin 监听此消息后，转发给 liq
     */
    PC_MATCH_SAME_SIDE_CLOSE_ORDER_CANCELLED(38, "PC_MATCH_SAME_SIDE_CLOSE_ORDER_CANCELLED"),

    PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED(39, "PC_MATCH_SAME_SIDE_CLOSE_ORDER_ALL_CANCELLED"),
    PC_ORDER_REBASE(40, "PC_ORDER_REBASE"),


    // TODO zw，新增防御性事件
//    /**
//     * 重置深度，由撮合发出，将重置的深度全部发出
//     */
//    PC_MATCH_ORDER_BOOK_RESET(31, "PC_MATCH_ORDER_BOOK_RESET"),
//    /**
//     * 撮合中 pending new 消息的委托 被忽略，因为已经在队列中存在，由撮合发出，防御性消息
//     */
//    PC_MATCH_ORDER_IGNORE_4_ALREADY_IN_QUEUE(32, "PC_MATCH_ORDER_IGNORE_4_ALREADY_IN_QUEUE"),
//    /**
//     * 撮合中 pending new 消息的委托 被忽略，因为此消息内容显示该委托已经完成，由撮合发出，防御性消息
//     */
//    PC_MATCH_ORDER_IGNORE_4_COMPLETED(33, "PC_MATCH_ORDER_IGNORE_4_COMPLETED"),
//    /**
//     * 撮合中 pending cancel 消息的委托 被忽略，因为此消息内容显示该委托不在队列中，不知道应该取消多少量，因此下游应该全部取消
//     * 同时，需要查找不在队列中的原因
//     */
//    PC_MATCH_ORDER_CANCEL_IGNORE_4_NOT_IN_QUEUE(34, "PC_MATCH_ORDER_CANCEL_IGNORE_4_NOT_IN_QUEUE"),

    //

    ;

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
