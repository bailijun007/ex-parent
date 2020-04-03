package com.hp.sh.expv3.pc.trade.constant;

import com.hp.sh.expv3.pc.trade.mq.BbMatchMqConsumer;
import com.hp.sh.expv3.pc.trade.service.impl.SupportBbGroupIdsJobServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public class MsgConstant {
    private static final Logger logger = LoggerFactory.getLogger(MsgConstant.class);


    @Autowired
    private SupportBbGroupIdsJobServiceImpl supportBbGroupIdsJobService;

    public static final String TAG_PC_MATCH = "PC_MATCH";

    public static final String getMatchTopic(String env,String asset, String symbol){
        logger.warn("env:{}", env);
        if (env.equals("bai")||env.equals("pcdev")||env.equals("pcdev2")||env.equals("pcdev3")) {
            String namespacea = "exp_v3%bbMatch_" + asset + "__" + symbol;
            logger.warn("namespacea:{}", namespacea);
            return namespacea;
        }else if (env.equals("pcprod")||env.equals("pcprod2")||env.equals("pcprod3")){
            String namespacea = "exp%bbMatch_" + asset + "__" + symbol;
            logger.warn("namespacea:{}", namespacea);
            return namespacea;
        }
        return null;
    }



}
