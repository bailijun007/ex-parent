package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;

import java.util.List;
import java.util.Set;

/**
 * @author BaiLiJun  on 2020/3/24
 */
public interface BbAccountLogExtService {
    List<BbAccountLogExtVo> listBbAccountLogs(Long userId, Set<String> assets, List<String> symbols, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer pageSize);

    List<BbAccountLogExtVo> listBbAccountLogsByPage(Long userId, Set<String>  assets, List<String> symbols, Integer historyType, Integer tradeType, Long lastId, Integer nextPage, Long startDate, Long endDate, Integer pageSize);

}
