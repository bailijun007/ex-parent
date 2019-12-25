package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordVo;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcAccountRecordExtendService {
    PcAccountRecordVo getPcAccountRecord(Long refId, String asset, Long userId, Long time);
}
