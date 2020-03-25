package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/24
 */
public interface BbAccountLogExtService {
    List<BbAccountLogExtVo> listBbAccountLogs(Long userId, String asset, String symbol, Integer tradeType, Long startDate, Long endDate, Integer nextPage, Integer lastOrderId, Integer pageSize);

}
