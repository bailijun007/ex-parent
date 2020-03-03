package com.hp.sh.expv3.match.enums;


import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 委托有效时间
 */
public enum BbOrderTimeInForceEnum implements EnumDescribe {

    /**
     * 一直有效
     */
    GOOD_TILL_CANCEL(0, "GOOD_TILL_CANCEL"),
    /**
     * 只做Maker
     */
    MAKER_ONLY(1, "MAKER_ONLY"),
    /**
     * 立即成交或取消，能成交多少是多少
     */
    IMMEDIATE_OR_CANCEL(2, "IMMEDIATE_OR_CANCEL"),
    /**
     * 全部成交或取消
     */
    FILL_OR_KILL(3, "FILL_OR_KILL"),
    //
    ;
    public static final Set<Integer> VALUES = EnumSet.allOf(BbOrderTimeInForceEnum.class).stream().map(BbOrderTimeInForceEnum::getCode).collect(Collectors.toSet());

    private int code;
    private String constant;

    private BbOrderTimeInForceEnum(int code, String constant) {
        this.code = code;
        this.constant = constant;
    }

    public int getCode() {
        return this.code;
    }

    public String getConstant() {
        return this.constant;
    }


}
