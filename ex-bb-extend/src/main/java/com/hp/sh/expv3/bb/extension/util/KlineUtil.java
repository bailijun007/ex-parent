package com.hp.sh.expv3.bb.extension.util;


import com.hp.sh.expv3.bb.extension.vo.KlineDataPo;

import java.math.BigDecimal;


/**
 * @author BaiLiJun  on 2020/3/11
 */
public class KlineUtil {


    public static String kline2ArrayData(KlineDataPo klineDataPo) {
        BigDecimal[] bigDecimals = new BigDecimal[6];
        bigDecimals[0] = new BigDecimal(klineDataPo.getOpenTime());
        bigDecimals[1] = klineDataPo.getOpen().stripTrailingZeros();
        bigDecimals[2] = klineDataPo.getHigh().stripTrailingZeros();
        bigDecimals[3] = klineDataPo.getLow().stripTrailingZeros();
        bigDecimals[4] = klineDataPo.getClose().stripTrailingZeros();
        bigDecimals[5] = klineDataPo.getVolume().stripTrailingZeros();
        final String s = JsonUtil.toJsonString(bigDecimals);
        return s;
    }


}
