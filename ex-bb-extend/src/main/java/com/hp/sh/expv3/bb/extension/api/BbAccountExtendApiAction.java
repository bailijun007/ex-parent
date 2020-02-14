package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbAccountExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author BaiLiJun  on 2020/2/12
 */
@RestController
public class BbAccountExtendApiAction implements BbAccountExtendApi {

    @Autowired
    private BbAccountExtService bbAccountExtService;

    @Override
    public void createBBAccount(Long userId, String asset) {
        bbAccountExtService.createBBAccount(userId, asset);
    }

    @Override
    public Boolean bbAccountExist(Long userId, String asset) {
        return bbAccountExtService.bbAccountExist(userId, asset);
    }

    @Override
    public BbAccountVo getBBAccount(Long userId, String asset) {
        return bbAccountExtService.getBBAccount(userId, asset);
    }

    @Override
    public BigDecimal getBalance(Long userId, String asset) {
        BbAccountVo bbAccount = bbAccountExtService.getBBAccount(userId, asset);
        if (bbAccount == null) {
            throw new ExException(BbExtCommonErrorCode.ACCOUNT_DOES_NOT_EXIST);
        }
        return bbAccount.getBalance();
    }
}
