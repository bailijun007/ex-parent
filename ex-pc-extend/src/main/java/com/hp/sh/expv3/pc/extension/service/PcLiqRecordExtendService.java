package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcLiqRecordVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/25
 */
public interface PcLiqRecordExtendService {

    PcLiqRecordVo getPcLiqRecord(Long refId, String asset, String symbol, Long userId, Long time);

    List<PcLiqRecordVo> listPcLiqRecord(String asset, String symbol, Long userId, List<Long> refIds);
}
