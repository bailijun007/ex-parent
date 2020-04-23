package com.hp.sh.expv3.pc.trade.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.pc.trade.pojo.PcKline;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public class PcKlineUtil {

    public static PcKline convertKlineData(String s, int freq, String asset, String symbol) {
        PcKline bbkLine = new PcKline();
        bbkLine.setFrequence(freq);
        bbkLine.setAsset(asset);
        bbkLine.setSymbol(symbol);
        JSONArray ja = JSON.parseArray(s);
        Long ms = ja.getLong(0);
        bbkLine.setTimestamp(ms);
        bbkLine.setOpenPrice(ja.getBigDecimal(1));
        bbkLine.setHighPrice(ja.getBigDecimal(2));
        bbkLine.setLowPrice(ja.getBigDecimal(3));
        bbkLine.setClosePrice(ja.getBigDecimal(4));
        bbkLine.setVolume(ja.getBigDecimal(5));
        return bbkLine;
    }


}
