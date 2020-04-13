package com.hp.sh.expv3.bb.extension.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/25
 */
public class BbAccountRecordConst {
    public static final int ACCOUNT_FUND_TO_BB = 3;        //从资金账户转入

    public static final int ACCOUNT_BB_TO_FUND = 4;        //转出至资金账户

    public static final int ACCOUNT_PC_TO_BB = 5;        //从永续合约转入,

    public static final int ACCOUNT_BB_TO_PC = 6;        //转出至永续合约

//    public static final int TRADE_BUY_IN = 10;			//买入
//
//    public static final int TRADE_SELL_OUT = 9;			//卖出

    public static final int TRADE_BUY_IN = 9;            //买入

    public static final int TRADE_SELL_OUT = 10;            //卖出

    public static final int TRADE_SELL_INCOME = 11;		//+卖出收入结算货币(USDT) ==>买入

    public static final int TRADE_SELL_RELEASE = 12;	//-卖出成交释放冻结资产(BTC) ==>卖出

    public static final List<Integer> ALL_TRADE_TYPE = new ArrayList<>();


    //买入集合
    public static final List<Integer> ALL_TRADE_BUY_IN = new ArrayList<>();

    //卖出集合
    public static final List<Integer> ALL_TRADE_SELL_OUT = new ArrayList<>();


    static {
        ALL_TRADE_TYPE.add(ACCOUNT_FUND_TO_BB);
        ALL_TRADE_TYPE.add(ACCOUNT_BB_TO_FUND);
        ALL_TRADE_TYPE.add(ACCOUNT_PC_TO_BB);
        ALL_TRADE_TYPE.add(ACCOUNT_BB_TO_PC);
        ALL_TRADE_TYPE.add(TRADE_SELL_OUT);
        ALL_TRADE_TYPE.add(TRADE_BUY_IN);
        ALL_TRADE_TYPE.add(TRADE_SELL_INCOME);
        ALL_TRADE_TYPE.add(TRADE_SELL_RELEASE);
    }

    static {
        ALL_TRADE_BUY_IN.add(TRADE_BUY_IN);
        ALL_TRADE_BUY_IN.add(TRADE_SELL_INCOME);
    }

    static {
        ALL_TRADE_SELL_OUT.add(TRADE_SELL_OUT);
        ALL_TRADE_SELL_OUT.add(TRADE_SELL_RELEASE);
    }


}
