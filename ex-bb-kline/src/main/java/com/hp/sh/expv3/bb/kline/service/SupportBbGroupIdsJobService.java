package com.hp.sh.expv3.bb.kline.service;

import com.hp.sh.expv3.bb.kline.pojo.BBSymbol;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/3/18
 */
public interface SupportBbGroupIdsJobService {
    public Map<Integer, List<BBSymbol>> listSymbols();
}
