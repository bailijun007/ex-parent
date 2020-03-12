package com.hp.sh.expv3.bb.kline.service;

import com.hp.sh.expv3.bb.kline.pojo.BbTradeVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/11
 */
public interface BbTradeExtService {
    List<BbTradeVo> queryByTimeInterval(String asset, String symbol, long startTimeInMs, long endTimeInMs);
}
