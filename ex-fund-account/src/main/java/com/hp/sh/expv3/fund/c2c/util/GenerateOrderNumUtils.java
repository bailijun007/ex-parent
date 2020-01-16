package com.hp.sh.expv3.fund.c2c.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.time.Instant;

/**
 * @author BaiLiJun  on 2020/1/13
 */
public class GenerateOrderNumUtils {

    //生成并返回订单编号
    public static String getOrderNo(long userId) {
        String s = "";
        for (int i = 0; i < 10; i++) {
            int random = (int) (Math.random() * 10);
            s += random;
        }
        String prefix = "c";
        String sn = prefix  + s+"-"+userId;

        return sn;
    }
    


}
