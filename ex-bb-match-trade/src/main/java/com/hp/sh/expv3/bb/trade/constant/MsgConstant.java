package com.hp.sh.expv3.bb.trade.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BaiLiJun  on 2020/3/31
 */
public class MsgConstant {

    private static final Logger logger = LoggerFactory.getLogger(MsgConstant.class);

    public static final String TAG_BB_MATCH = "BB_MATCH";

    public final String getMatchTopic(String env, String asset, String symbol) {
        logger.warn("env:{}", env);
        if (env.equals("bai")||env.equals("bbdev")||env.equals("bbdev2")||env.equals("bbdev3")) {
            String namespacea = "exp_v3%bbMatch_" + asset + "__" + symbol;
            logger.warn("namespacea:{}", namespacea);
            return namespacea;
        }else if (env.equals("bbprod")||env.equals("bbprod2")||env.equals("bbprod3")){
            String namespacea = "exp%bbMatch_" + asset + "__" + symbol;
            logger.warn("namespacea:{}", namespacea);
            return namespacea;
        }
        return null;
    }


}
