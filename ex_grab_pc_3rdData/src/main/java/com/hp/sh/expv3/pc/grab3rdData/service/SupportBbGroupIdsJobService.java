package com.hp.sh.expv3.pc.grab3rdData.service;

import com.hp.sh.expv3.pc.grab3rdData.pojo.PcSymbol;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/18
 */
public interface SupportBbGroupIdsJobService {
    public Map<Integer, List<PcSymbol>> listSymbols();

    List<PcSymbol> getSymbols();

}
