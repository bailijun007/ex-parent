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



}
