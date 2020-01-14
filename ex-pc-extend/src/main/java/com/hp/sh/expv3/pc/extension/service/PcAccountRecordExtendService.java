package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcAccountRecordExtendService {
    PcAccountRecordVo getPcAccountRecord(Long refId, String asset, Long userId, Long time);

    List<PcAccountRecordVo> listPcAccountRecord(List<Long> refIds, String asset, Long userId);
}
