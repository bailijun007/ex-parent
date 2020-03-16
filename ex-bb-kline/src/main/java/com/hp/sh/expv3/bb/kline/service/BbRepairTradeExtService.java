package com.hp.sh.expv3.bb.kline.service;

import com.hp.sh.expv3.bb.kline.vo.BbRepairTradeVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/16
 */
public interface BbRepairTradeExtService {
    List<BbRepairTradeVo> listRepairTrades(String asset, String symbol, long ms, long maxMs);
}
