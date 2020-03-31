package com.hp.sh.expv3.bb.kline.constant;

import com.hp.sh.expv3.bb.kline.service.impl.SupportBbGroupIdsJobServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public class MsgConstant {

    @Autowired
    private SupportBbGroupIdsJobServiceImpl supportBbGroupIdsJobService;

    public static final String EVENT_TOPIC = "exp_v3%bbMatch_USDT__ETC_USDT";

    public static final String getMatchTopic(String asset, String symbol){
        return "exp_v3%bbMatch_"+asset+"__"+symbol;
    }



}
