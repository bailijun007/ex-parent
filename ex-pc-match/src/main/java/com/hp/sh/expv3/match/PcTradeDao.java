package com.hp.sh.expv3.match;

import com.hp.sh.expv3.match.bo.PcTradeBo;

import java.util.List;

public interface PcTradeDao {

    void insert(PcTradeBo trade);

    void insert(String asset, String symbol, List<PcTradeBo> trades);

}