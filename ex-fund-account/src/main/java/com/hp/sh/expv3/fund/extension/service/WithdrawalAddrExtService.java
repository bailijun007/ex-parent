package com.hp.sh.expv3.fund.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrParam;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalAddrVo;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface WithdrawalAddrExtService {

    WithdrawalAddrVo getAddressByUserIdAndAsset(Long userId, String asset);

    PageResult<WithdrawalAddrVo> pageQueryWithdrawalAddrList(Long userId, String asset, Long pageNo, Integer pageSize, Integer enabled);
}
