package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;

/**
 * @author BaiLiJun  on 2020/2/13
 */
public interface BbAccountExtService {
    void createBBAccount(Long userId, String asset);

    Boolean bbAccountExist(Long userId, String asset);

    BbAccountVo getBBAccount(Long userId, String asset);
}
