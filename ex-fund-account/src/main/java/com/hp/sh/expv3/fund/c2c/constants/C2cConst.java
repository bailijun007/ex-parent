package com.hp.sh.expv3.fund.c2c.constants;

/**
 * @author BaiLiJun  on 2020/1/13
 */
public final class C2cConst {
    //1 买入，-1 卖出
    public static final int C2C_SELL = -1;
    public static final int C2C_BUY = 1;

//    0-待支付，1-支付成功，2-支付失败,3:已取消,
    public static final int C2C_PAY_STATUS_NO_PAYMENT = 0;
    public static final int C2C_PAY_STATUS_PAY_SUCCESS = 1;
    public static final int C2C_PAY_STATUS_PAYMENT_FAILED = 2;
    public static final int C2C_PAY_STATUS_CANCELED  = 3;

    //支付状态描述
    public static final String C2C_PAY_STATUS_DESC_RECHARGE = "c2c充值";
    public static final String C2C_PAY_STATUS_DESC_WITHDRAWAL = "c2c体现";
    public static final String C2C_PAY_STATUS_DESC_PAY_OVERTIME = "支付超时";

    //审批状态：4-审批中, 5-审批通过, 6-拒绝
    public static final int C2C_APPROVAL_STATUS_IN_AUDIT  = 4;

    public static final int C2C_APPROVAL_STATUS_PASS  = 5;

    public static final int C2C_APPROVAL_STATUS_REJECTED  = 6;


    //同步状态 0：未同步，1：已同步
    public static final int C2C_SYNCH_STATUS_FALSE  = 0;
    public static final int C2C_SYNCH_STATUS_TRUE  = 1;



    public static final String LOCKED_ASSET="USDT";


}
