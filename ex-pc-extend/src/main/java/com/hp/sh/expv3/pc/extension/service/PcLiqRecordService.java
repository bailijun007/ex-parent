package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcLiqRecordVo;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public interface PcLiqRecordService {
    PcLiqRecordVo getPcLiqRecord(Long refId, String asset, String symbol, Long userId, Long time);
}
