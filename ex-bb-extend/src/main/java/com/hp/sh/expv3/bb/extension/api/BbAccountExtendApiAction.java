package com.hp.sh.expv3.bb.extension.api;

import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/2/12
 */
@RestController
public class BbAccountExtendApiAction implements BbAccountExtendApi{


    @Override
    public int createBBAccount(Long userId, String asset) {
        return 0;
    }
}
