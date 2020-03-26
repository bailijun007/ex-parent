package com.hp.sh.expv3.bb.extension.constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/9
 */
public class BbextendConst {
    //historyType 1.最近两天,2.两天到三个月
    public static final Integer HISTORY_TYPE_LAST_TWO_DAYS = 1;

    public static final Integer HISTORY_TYPE_LAST_THREE_MONTHS = 2;

    public static final Integer TRADE_TYPE_ALL = 0;

    public static final Map<Integer, List<Integer>> TRADE_TYPE_MAP = new HashMap<Integer, List<Integer>>();

    /**
     * 买入和买出
     */
    public static final Integer TRADE_BUYIN_AND_BUYOUT = -1;


    /**
     * 转出
     */
    public static final Integer ROLLOFF_TO_FUND_ACCOUNT_AND_TO_PC_ACCOUNT= -2;


    /**
     * 转入
     */
    public static final Integer INTO_FUND_ACCOUNT_AND_PC_ACCOUNT= -3;


    static {
        TRADE_TYPE_MAP.put(TRADE_BUYIN_AND_BUYOUT, new ArrayList<Integer>() {{
            add(BbAccountLogConst.TYPE_BUY_IN);
            add(BbAccountLogConst.TYPE_SELL_OUT);
        }});

        TRADE_TYPE_MAP.put(ROLLOFF_TO_FUND_ACCOUNT_AND_TO_PC_ACCOUNT, new ArrayList<Integer>() {{
            add(BbAccountLogConst.CHANGE_OUT_FUND_ACCOUNT);
            add(BbAccountLogConst.CHANGE_OUT_PC_ACCOUNT);
        }});

        TRADE_TYPE_MAP.put(INTO_FUND_ACCOUNT_AND_PC_ACCOUNT, new ArrayList<Integer>() {{
            add(BbAccountLogConst.CHANGE_INTO_FUND_ACCOUNT);
            add(BbAccountLogConst.CHANGE_INTO_PC_ACCOUNT);
        }});

    }

}
