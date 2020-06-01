package com.hp.sh.expv3.pc.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionStatVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionTotalVo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author BaiLiJun  on 2019/12/16
 */
public interface PcPositionExtendService {
    /**
     * 获取仓位保证金
     *
     * @param userId
     * @param asset
     * @return
     */
    BigDecimal getPosMargin(Long userId, String asset);

    PageResult<PcPositionVo> pageQueryPositionList(Long userId, String asset, String symbol, Long posId, Integer liqStatus, Integer pageNo, Integer pageSize, Long startTime, Long endTime);

    List<PcPositionVo> findActivePosition(Long userId, String asset, String symbol, Long startTime, Long endTime);

    //通过用户id，资产，交易对查询均价
    BigDecimal getAvgPrice(Long userId, String asset, String symbol);

    List<PcPositionVo> selectPosByAccount(Long userId, String asset, String symbol, Long startTime,Long endTime);

    List<PcSymbolPositionStatVo> getSymbolPositionStat(String asset, String symbol);

	PcSymbolPositionTotalVo getSymbolPositionTotal(String asset, String symbol);
}
