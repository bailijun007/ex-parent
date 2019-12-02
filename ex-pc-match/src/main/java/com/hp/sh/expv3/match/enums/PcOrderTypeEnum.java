package com.hp.sh.expv3.match.enums;

/**
 * 永续合约类型
 */
public enum PcOrderTypeEnum implements EnumDescribe {
    /**
     * 限价订单
     */
    LIMIT(1, "LIMIT"),
    /**
     * 市价订单
     */
    MARKET(2, "MARKET"),
    /**
     * 限价止盈订单
     */
    TAKE_PROFIT_LIMIT(3, "TAKE_PROFIT_LIMIT"),
    /**
     * 限价止损订单
     */
    STOP_LIMIT(4, "STOP_LIMIT"),
    /**
     * 市价止盈订单
     */
    TAKE_PROFIT_MARKET(5, "TAKE_PROFIT_MARKET"),
    /**
     * 市价止损订单
     */
    STOP_MARKET(6, "STOP_MARKET"),;

    private int code;
    private String constant;

    private PcOrderTypeEnum(int code, String constant) {
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
