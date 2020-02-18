package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbOrderTradeExtService {
    BbOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime);
}
