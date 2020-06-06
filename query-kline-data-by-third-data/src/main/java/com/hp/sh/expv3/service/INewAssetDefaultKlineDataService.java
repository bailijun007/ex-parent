package com.hp.sh.expv3.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/6/4
 */

public interface INewAssetDefaultKlineDataService {


    void getDefaultKlineData(BigDecimal lastPrice,Long time,String asset,String symbol);
}
