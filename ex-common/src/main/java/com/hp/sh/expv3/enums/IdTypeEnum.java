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
    PC_MATCH(8),// 永续合约撮合ID，由PC撮合生成
    PC_TRADE(9), // 永续合约成交，由PC撮合生成
    BB_MATCH(10),// 币币合约撮合，由BB撮合生成
    BB_TRADE(11), // 币币合约成交，由BB撮合生成
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
