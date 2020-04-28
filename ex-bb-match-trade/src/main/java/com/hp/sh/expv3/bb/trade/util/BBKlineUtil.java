package com.hp.sh.expv3.bb.trade.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.bb.trade.pojo.BBKLine;
import com.hp.sh.expv3.bb.trade.pojo.BBSymbol;
import com.hp.sh.expv3.bb.trade.service.SupportBbGroupIdsJobService;
import com.hupa.exp.common.tool.format.JsonUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public class BBKlineUtil {

    public static BBKLine convertKlineData(String s, int freq, String asset, String symbol) {
        BBKLine bbkLine = new BBKLine();
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
