package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbTradeExtService {
    List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime);
}
