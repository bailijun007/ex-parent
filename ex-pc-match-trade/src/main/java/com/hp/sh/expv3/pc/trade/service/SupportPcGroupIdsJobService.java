package com.hp.sh.expv3.pc.trade.service;

import com.hp.sh.expv3.pc.trade.pojo.BBSymbol;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/18
 */
public interface SupportPcGroupIdsJobService {
    public Map<Integer, List<BBSymbol>> listSymbols();
    List<BBSymbol> getSymbols();
}
