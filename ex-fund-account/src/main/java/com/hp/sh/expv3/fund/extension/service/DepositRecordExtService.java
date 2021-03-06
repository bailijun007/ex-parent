package com.hp.sh.expv3.fund.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 充值记录扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
public interface DepositRecordExtService {


    public List<DepositRecordHistoryVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize,Integer pageStatus) ;

    PageResult<DepositRecordHistoryVo> pageQueryDepositRecordHistory(Long userId, String asset, Integer pageNo, Integer pageSize);

    BigDecimal queryTotalNumber(String asset, Integer payStatus);
}
