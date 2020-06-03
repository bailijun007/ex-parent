package com.hp.sh.expv3.bb.extension.service;

import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeDetailVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbUserOrderTrade;

import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
public interface BbOrderTradeExtService {
    BbOrderTradeVo selectLessTimeTrade(String asset, String symbol, Long statTime);

    List<BbOrderTradeVo> selectAllTradeListByUser(String asset, String symbol, Long userId);

    List<BbOrderTradeVo> queryOrderTrade(Long userId, List<Long> orderIdList);

    List<BbUserOrderTrade> selectTradeListByUserId(String asset, String symbol, Long startTime, Long endTime, Long userId, Long id);

    List<BbOrderTradeDetailVo> selectPcFeeCollectByAccountId(String asset, String symbol, Long userId, Long statTime, Long endTime);

    List<BbOrderTradeVo> queryByIds(List<Long> refIds, String asset, ArrayList<String> symbols , Long startDate, Long endDate);

    List<BbOrderTradeDetailVo> queryHistory(Long userId, String asset, String symbol, Long lastTradeId, Integer nextPage, Integer pageSize, Long startTime, Long endTime);

}

