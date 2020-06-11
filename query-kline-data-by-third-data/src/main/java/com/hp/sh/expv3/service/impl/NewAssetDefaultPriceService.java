package com.hp.sh.expv3.service.impl;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/6/5
 */
@Service
public class NewAssetDefaultPriceService {

//    @Value("${max.price}")
    private BigDecimal maxPrice = new BigDecimal(0.16);

//    @Value("${start.price}")
    private BigDecimal startPrice = new BigDecimal(0.155);

//    @Value("${min.price}")
    private BigDecimal minPrice = new BigDecimal(0.122);

//    @Value("${stepMax.price}")
    private BigDecimal stepMaxPrice = new BigDecimal(+0.000015);
//    @Value("${stepMin.price}")
    private BigDecimal stepMinPrice = new BigDecimal(-0.000015);

//    @Value("${count}")
    private int count = 14400;

    public List<BigDecimal> buildPrice() {
        ArrayList<BigDecimal> price = new ArrayList<>(count);
        while (true) {
            BigDecimal nextPrice = nextPrice(stepMaxPrice, stepMinPrice);
            BigDecimal startPriceNew = startPrice.add(nextPrice);
            if (startPriceNew.compareTo(minPrice) >= 0 && startPriceNew.compareTo(maxPrice) <= 0) {
                price.add(startPriceNew);
                startPrice = startPriceNew;
            } else {
                continue;
            }
            if (price.size() == count) {
                break;
            }
        }

        return price;
    }

    public static BigDecimal nextPrice(BigDecimal stepMaxPrice, BigDecimal stepMinPrice) {
        BigDecimal min = stepMinPrice.min(stepMaxPrice);
        BigDecimal max = stepMinPrice.max(stepMaxPrice);
        double width = max.doubleValue() - min.doubleValue();
        double pDouble = RandomUtils.nextDouble(0, width) + min.doubleValue();
        return new BigDecimal(pDouble);
    }

public static void main(String[] args) {
    final List<BigDecimal> bigDecimals = new NewAssetDefaultPriceService().buildPrice();
    for (BigDecimal bigDecimal : bigDecimals) {
        System.out.println(bigDecimal);
    }
}

}
