package com.hp.sh.expv3.fund.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface FundTransferExtService {

    PageResult<FundTransferExtVo> pageQueryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus);

    PageResult<FundTransferExtVo> queryAllUserHistory(Long userId, String asset, Integer pageNo,Integer pageSize);
}
