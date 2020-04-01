package com.hp.sh.expv3.enums;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 生成ID时需要，实体才需定义，非实体可使用None
 */
public enum IdTypeEnum {

    COMMON(0),// 通用，ID可能会重复
    USER(1),//用户
    PC_MATCH(8),// 永续合约撮合ID，由PC撮合生成
    PC_TRADE(9), // 永续合约成交，由PC撮合生成
    BB_MATCH(10),// 币币合约撮合，由BB撮合生成
    BB_TRADE(11), // 币币合约成交，由BB撮合生成
    
    //fund
    DEPOSIT_ADDR_ID(12),
    DEPOSIT_RECORD_ID(13),
    FUND_ACCOUNT_ID(14),
    FUND_ACCOUNT_RECORD_ID(15),
    FUND_TRANSFER_ID(16),
    WITHDRAWAL_ADDR_ID(17),
    WITHDRAWAL_RECORD_ID(18),
    C2C_ORDER_ID(19),
    
    //bb
    BB_TX_ID(20),
    BB_ACCOUNT_RECORD_ID(21),
    BB_ORDER_ID(22),
    BB_ORDER_LOG_ID(23),
    BB_ORDER_TRADE_ID(24),
    BB_ACTIVE_ORDER_ID(25),
    BB_MATCHED_TRADE_ID(26),
    BB_COLLECTOR_ACCOUNT_RECORD_ID(27),
    BB_ACCOUNT_LOG_ID(28),
    
    //pc
    PC_TX_ID(30),
    PC_ACCOUNT_ID(31),
    PC_ACCOUNT_LOG_ID(32),
    PC_ACCOUNT_RECORD_ID(33),
    PC_ACCOUNT_SYMBOL_ID(34),
    PC_LIQ_RECORD_ID(35),
    PC_ORDER_ID(36),
    PC_ORDER_LOG_ID(37),
    PC_ORDER_TRADE_ID(38),
    PC_POSITION_ID(39),
    PC_ACTIVE_ORDER_ID(40),
    PC_ACTIVE_POSITION_ID(41), 
    PC_COLLECTOR_ACCOUNT_RECORD_ID(42),
    
    ;

    public final int value;

    public int getValue() {
        return value;
    }
 
    IdTypeEnum(int v) {
        value = v;
    }

    protected static final EnumSet<IdTypeEnum> ALL_VALUES = EnumSet.allOf(IdTypeEnum.class);
    protected static final Map<Integer, IdTypeEnum> code2Enum = ALL_VALUES.stream().collect(Collectors.toMap(IdTypeEnum::getValue, Function.identity()));

    public static final IdTypeEnum getByCode(int value) {
        return code2Enum.getOrDefault(value, null);
    }

    public static IdTypeEnum fromNum(int v) {
        for (IdTypeEnum vv : ALL_VALUES) {
            if (vv.value == v) return vv;
        }
        return null;
    }
}
