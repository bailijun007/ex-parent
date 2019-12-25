package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcAccountSymbolVo;

/**
 * @author BaiLiJun  on 2019/12/23
 */
public interface PcAccountSymbolExtendService {
    PcAccountSymbolVo getPcAccountSymbol(Long userId, String asset, String symbol);
}
