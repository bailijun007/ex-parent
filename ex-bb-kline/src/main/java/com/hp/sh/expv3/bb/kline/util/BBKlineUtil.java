package com.hp.sh.expv3.bb.kline.util;

import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hupa.exp.common.tool.format.JsonUtil;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public class BBKlineUtil {


    public static String buildKlineData(BBKLine bbkLine) {
        BigDecimal[] bigDecimals = new BigDecimal[6];
//        bigDecimals[0] = new BigDecimal(bbkLine.getMs());
        bigDecimals[1] = bbkLine.getHigh() == null ? BigDecimal.ZERO : bbkLine.getHigh();
        bigDecimals[2] = bbkLine.getLow() == null ? BigDecimal.ZERO : bbkLine.getLow();
        bigDecimals[3] = bbkLine.getOpen() == null ? BigDecimal.ZERO : bbkLine.getOpen();
        bigDecimals[4] = bbkLine.getClose() == null ? BigDecimal.ZERO : bbkLine.getClose();
        bigDecimals[5] = bbkLine.getVolume() == null ? BigDecimal.ZERO : bbkLine.getVolume();
        final String s = JsonUtil.toJsonString(bigDecimals);
        return s;
    }

    public static BBKLine convertKlineData(String s) {
        BBKLine bbkLine=new BBKLine();
        String[] array = s.split(",");
        final long ms = Long.parseLong(array[0].substring(1));
//        final long minute = TimeUnit.MINUTES.toMillis(l);
        bbkLine.setFrequence(1);
//        bbkLine.setMs(ms);
        bbkLine.setOpen(new BigDecimal(array[1]));
        bbkLine.setHigh(new BigDecimal(array[2]));
        bbkLine.setLow(new BigDecimal(array[3]));
        bbkLine.setClose(new BigDecimal(array[4]));
        bbkLine.setVolume(new BigDecimal(array[5].substring(0,array[5].length()-1)));

        return bbkLine;
    }

    /**
     * 分钟时间戳转成毫秒时间戳
     */
    public static Long minutesToMillis(Long minute){
       return TimeUnit.MINUTES.toMillis(minute);
    }

}
