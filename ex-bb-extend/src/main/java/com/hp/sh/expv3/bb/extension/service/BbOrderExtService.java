package com.hp.sh.expv3.bb.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.bb.extension.vo.BbHistoryOrderVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbOrderExtService {
    PageResult<BbOrderVo> queryAllBbOrederHistory(Long userId, String asset, Integer pageNo, Integer pageSize);

    List<BbHistoryOrderVo> queryHistoryOrderList(Long userId, String asset, String symbol, Integer bidFlag, Integer pageSize, Long lastOrderId, Integer nextPage);

    BigDecimal getLockAsset(Long userId, String asset);

    PageResult<BbHistoryOrderVo> queryBbActiveOrderList(Long userId, String asset, String symbol, Integer bidFlag, Integer pageSize, Long lastOrderId, Integer nextPage);

    List<BbHistoryOrderVo> queryOrderList(Long userId, List<String> assetList, List<String> symbolList, Long gtOrderId, Long ltOrderId, Integer count, List<Integer> statusList);
}
