package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcAccountCoreService {
    public BigDecimal getBalance(Long userId, String asset);

    public PcAccountExtVo findContractAccount(Long userId, String asset);

}
