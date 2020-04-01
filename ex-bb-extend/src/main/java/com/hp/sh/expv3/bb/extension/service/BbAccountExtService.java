package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbAccountExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;

/**
 * @author BaiLiJun  on 2020/2/13
 */
public interface BbAccountExtService {
    void createBBAccount(Long userId, String asset);

    Boolean bbAccountExist(Long userId, String asset);

    BbAccountExtVo getBBAccount(Long userId, String asset);

    BbAccountExtVo getNewBBAccount(Long userId, String asset);
}
