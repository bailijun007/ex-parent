package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public interface PcAccountLogExtendService {

    List<PcAccountLogVo> getPcAccountLogList(Long userId, String asset, Integer tradeType, Integer historyType, Long startDate, Long endDate, String symbol);
    void save(PcAccountLogVo pcAccountLogVo);

    PcAccountLogVo getPcAccountLog(PcAccountLogVo pcAccountLogVo);

}
