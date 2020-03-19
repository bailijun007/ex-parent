package com.hp.sh.expv3.bb.kline.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hp.sh.expv3.bb.kline.constant.BbKLineKey;
import com.hp.sh.expv3.bb.kline.pojo.BBKLine;
import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;
import com.hp.sh.expv3.bb.kline.service.SupportBbGroupIdsJobService;
import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;
import com.hp.sh.expv3.config.redis.RedisUtil;
import com.hupa.exp.common.tool.format.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public class BBKlineUtil {


    public static String kline2ArrayData(BBKLine bbkLine) {
        BigDecimal[] bigDecimals = new BigDecimal[6];
        bigDecimals[0] = new BigDecimal(bbkLine.getMs());
        bigDecimals[1] = bbkLine.getOpen().stripTrailingZeros();
        bigDecimals[2] = bbkLine.getHigh().stripTrailingZeros();
        bigDecimals[3] = bbkLine.getLow().stripTrailingZeros();
        bigDecimals[4] = bbkLine.getClose().stripTrailingZeros();
        bigDecimals[5] = bbkLine.getVolume().stripTrailingZeros();
        final String s = JsonUtil.toJsonString(bigDecimals);
        return s;
    }

    public static BBKLine convert2KlineData(String s, int freq) {
        BBKLine bbkLine = new BBKLine();
        bbkLine.setFrequence(freq);

        final JSONArray ja = JSON.parseArray(s);
        final Long ms = ja.getLong(0);
        bbkLine.setMs(ms);
        bbkLine.setMinute(TimeUnit.MILLISECONDS.toMinutes(ms));
        bbkLine.setOpen(ja.getBigDecimal(1));
        bbkLine.setHigh(ja.getBigDecimal(2));
        bbkLine.setLow(ja.getBigDecimal(3));
        bbkLine.setClose(ja.getBigDecimal(4));
        bbkLine.setVolume(ja.getBigDecimal(5));
        return bbkLine;
    }


//    public static List<BBSymbol> listSymbol(RedisUtil metadataRedisUtil) {
//        final Map<String, BBSymbol> key2Value = metadataRedisUtil.hgetAll(BbKLineKey.BB_SYMBOL, BBSymbol.class);
//        List<BBSymbol> list = key2Value.values().stream().collect(Collectors.toList());
//        return list;
//    }

    public static List<BBSymbol> listSymbols(SupportBbGroupIdsJobService supportBbGroupIdsJobService, Set<Integer> supportBbGroupIds) {
        List<BBSymbol> bbSymbols = null;
        final Map<Integer, List<BBSymbol>> map = supportBbGroupIdsJobService.listSymbols();
        for (Integer integer : map.keySet()) {
            if (supportBbGroupIds.contains(integer)) {
                bbSymbols = map.get(integer);
            }
        }
        return bbSymbols;
    }

    public static List<BBSymbol> filterBbSymbols(List<BBSymbol> bbSymbols, Set<Integer> supportBbGroupIds) {
        return bbSymbols.stream()
                .filter(symbol -> supportBbGroupIds.contains(symbol.getBbGroupId()))
                .collect(Collectors.toList());
    }

}
