package com.hp.sh.expv3.pc.extension.constant;

import com.hp.sh.expv3.pc.msg.PcAccountLog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用常量类
 *
 * @author BaiLiJun  on 2020/1/11
 */
public final class ExtCommonConstant {

    public static final Integer TRADE_TYPE_ALL = 0;
    /**
     * 转入和转出
     */
    public static final Integer TRADE_TYPE_DEPOSITE_AND_WITHDRAW = -1;
    /**
     * 开平多空仓
     */
    public static final Integer TRADE_TYPE_MAKE_ORDER = -2;
    /**
     * 修改保证金
     */
    public static final Integer TRADE_TYPE_CHANGE_MARGIN = -3;
    public static final Map<Integer, List<Integer>> TRADE_TYPE_MAP = new HashMap<Integer, List<Integer>>() {{
        put(TRADE_TYPE_CHANGE_MARGIN, new ArrayList<Integer>() {{
            add(PcAccountLog.TYPE_ADD_TO_MARGIN);
            add(PcAccountLog.TYPE_REDUCE_MARGIN);
            add(PcAccountLog.TYPE_AUTO_ADD_MARGIN);
            add(PcAccountLog.TYPE_LEVERAGE_ADD_MARGIN);
        }});
        put(TRADE_TYPE_MAKE_ORDER, new ArrayList<Integer>() {{
            add(PcAccountLog.TYPE_TRAD_OPEN_LONG);
            add(PcAccountLog.TYPE_TRAD_OPEN_SHORT);
            add(PcAccountLog.TYPE_TRAD_CLOSE_LONG);
            add(PcAccountLog.TYPE_TRAD_CLOSE_SHORT);
        }});
        put(TRADE_TYPE_DEPOSITE_AND_WITHDRAW, new ArrayList<Integer>() {{
            add(PcAccountLog.TYPE_FUND_TO_PC);
            add(PcAccountLog.TYPE_PC_TO_FUND);
        }});
        put(TRADE_TYPE_LIQ, new ArrayList<Integer>() {{
            add(PcAccountLog.TYPE_LIQ_LONG);
            add(PcAccountLog.TYPE_LIQ_SHORT);
        }});
    }};
    /**
     * 强平多空仓
     */
    public static final Integer TRADE_TYPE_LIQ = -4;

    public static final Integer HISTORY_TYPE_LAST_TWO_DAYS = 1;

    //historyType 1.最近两天,2.两天到三个月
    public static final Integer HISTORY_TYPE_LAST_THREE_MONTHS = 2;

}
