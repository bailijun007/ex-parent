package com.hp.sh.expv3.bb.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.PcAccountLogVo;

import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public interface PcAccountLogExtendService {

    PageResult<PcAccountLogVo> pageQueryPcAccountLogList(Long userId, String asset, Integer tradeType, Integer historyType, Long startDate, Long endDate, String symbol, Integer pageNo, Integer pageSize);
    void save(PcAccountLogVo pcAccountLogVo);

    PcAccountLogVo getPcAccountLog(PcAccountLogVo pcAccountLogVo);

}
