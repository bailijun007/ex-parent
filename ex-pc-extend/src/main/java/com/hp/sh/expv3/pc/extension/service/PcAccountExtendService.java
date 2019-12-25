package com.hp.sh.expv3.pc.extension.service;

import com.hp.sh.expv3.pc.extension.vo.PcAccountExtVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcAccountExtendService {
    public BigDecimal getBalance(Long userId, String asset);

    public PcAccountExtVo findContractAccount(Long userId, String asset);

    public List<PcAccountExtVo> findContractAccountList(Long userId, String asset);

}
