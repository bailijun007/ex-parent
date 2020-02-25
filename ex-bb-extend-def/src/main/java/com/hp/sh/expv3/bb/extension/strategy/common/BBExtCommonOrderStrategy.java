package com.hp.sh.expv3.bb.extension.strategy.common;

import com.hp.sh.expv3.bb.extension.strategy.BbOrderStrategy;
import com.hp.sh.expv3.bb.extension.strategy.data.BbOrderTrade;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/25
 */
@Component
public class BBExtCommonOrderStrategy implements BbOrderStrategy {

    public BigDecimal calcOrderMeanPrice(String asset, String symbol, List<? extends BbOrderTrade> tradeList){
        if(tradeList==null || tradeList.isEmpty()){
            return BigDecimal.ZERO;
        }
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal vols = BigDecimal.ZERO;
        for(BbOrderTrade trade : tradeList){
            amount = amount.add(calcAmount(trade.getVolume(), trade.getPrice()));
            vols = vols.add(trade.getVolume());
        }
        if(vols.compareTo(BigDecimal.ZERO)==0){
            return BigDecimal.ZERO;
        }
        BigDecimal meanPrice = calcEntryPrice(vols, amount);
        return meanPrice;
    }


    public static BigDecimal calcAmount(BigDecimal volume, BigDecimal price){
        return volume.multiply(price);
    }

    private static BigDecimal calcEntryPrice(BigDecimal volume, BigDecimal amt) {
        return amt.divide(volume, 18,  RoundingMode.UP).stripTrailingZeros();
    }
}
