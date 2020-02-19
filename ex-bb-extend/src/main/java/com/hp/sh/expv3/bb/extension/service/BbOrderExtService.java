package com.hp.sh.expv3.bb.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.bb.extension.vo.HistoryOrderVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbOrderExtService {
    PageResult<BbOrderVo> queryAllBbOrederHistory(Long userId, String asset, Integer pageNo, Integer pageSize);

    List<HistoryOrderVo> queryHistoryOrderList(Long userId, String asset, String symbol);
}
