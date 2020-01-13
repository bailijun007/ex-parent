package com.hp.sh.expv3.fund.c2c.util;

import java.time.Instant;

/**
 * @author BaiLiJun  on 2020/1/13
 */
public class GenerateOrderNumUtils {

    //生成并返回订单编号
    public static String getOrderNo(long userId) {
        String s = "";
        for (int i = 0; i < 4; i++) {
            int random = (int) (Math.random() * 10);
            s += random;
        }
        Instant instant = Instant.now();
        long timestamp = instant.toEpochMilli();
        String prefix = "c2c";
        String sn = prefix + timestamp + s+"-"+userId;

        return sn;
    }

}
