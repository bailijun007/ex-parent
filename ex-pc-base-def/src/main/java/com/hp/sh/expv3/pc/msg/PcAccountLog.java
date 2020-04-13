package com.hp.sh.expv3.pc.msg;

import com.hp.sh.expv3.pc.constant.LogType;

/**
 * 账户日志
 *
 * @author wangjg
 */
@Deprecated
public interface PcAccountLog {

    public static final int TYPE_TRAD_OPEN_LONG = LogType.TYPE_TRAD_OPEN_LONG;        //成交开多
    public static final int TYPE_TRAD_OPEN_SHORT = LogType.TYPE_TRAD_OPEN_SHORT;        //成交开空
    public static final int TYPE_TRAD_CLOSE_LONG = LogType.TYPE_TRAD_CLOSE_LONG;        //成交平多
    public static final int TYPE_TRAD_CLOSE_SHORT = LogType.TYPE_TRAD_CLOSE_SHORT;        //成交平空

    public static final int TYPE_FUND_TO_PC = LogType.TYPE_ACCOUNT_FUND_TO_PC;            //转入
    public static final int TYPE_PC_TO_FUND = LogType.TYPE_ACCOUNT_PC_TO_FUND;            //转出

    public static final int TYPE_ADD_TO_MARGIN = LogType.TYPE_ACCOUNT_ADD_TO_MARGIN;            //-手动追加保证金
    public static final int TYPE_REDUCE_MARGIN = LogType.TYPE_ACCOUNT_REDUCE_MARGIN;            //+减少保证金
    public static final int TYPE_AUTO_ADD_MARGIN = LogType.TYPE_ACCOUNT_AUTO_ADD_MARGIN;        //-自动追加保证金
    public static final int TYPE_LEVERAGE_ADD_MARGIN = LogType.TYPE_ACCOUNT_LEVERAGE_ADD_MARGIN;    //-调低杠杆追加保证金

    public static final int TYPE_LIQ_LONG = LogType.TYPE_LIQ_LONG;                //强平平多
    public static final int TYPE_LIQ_SHORT = LogType.TYPE_LIQ_SHORT;				//强平平空

}
