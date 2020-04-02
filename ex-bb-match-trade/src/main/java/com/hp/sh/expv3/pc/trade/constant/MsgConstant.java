package com.hp.sh.expv3.pc.trade.constant;

import com.hp.sh.expv3.pc.trade.service.impl.SupportBbGroupIdsJobServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public class MsgConstant {

    @Autowired
    private SupportBbGroupIdsJobServiceImpl supportBbGroupIdsJobService;

    public static final String TAG_BB_MATCH = "BB_MATCH";

    public static final String getMatchTopic(String asset, String symbol){
        return "exp_v3%bbMatch_"+asset+"__"+symbol;
    }



}
