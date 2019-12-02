package com.hp.sh.expv3.match.util;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 生成ID时需要，实体才需定义，非实体可使用None
 */
public enum IdTypeEnum {
    COMMON(0),// 通用
    ACCOUNT(1),// 账户
    TRANSACTION(2),// 事务,persist
    ORDER(3),// 委托
    TRADE(4),// 成交
    DEPOSIT(5),// 充币
    WITHDRAW(6),// 提币
    POSITION(7),// 仓位
    MATCH(8),// 撮合
    TRANSFER(9),// 资金划转
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
