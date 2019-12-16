package com.hp.sh.expv3.fund.extension.service;

import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface WithdrawalAddrExtService {
    String getAddressByUserIdAndAsset(Long userId, String asset);

    List<WithdrawalAddrVo> findWithdrawalAddr(Long userId, String asset);
}
