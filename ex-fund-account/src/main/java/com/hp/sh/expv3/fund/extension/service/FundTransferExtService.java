package com.hp.sh.expv3.fund.extension.service;

import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface FundTransferExtService {

    List<FundTransferExtVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus);
}
