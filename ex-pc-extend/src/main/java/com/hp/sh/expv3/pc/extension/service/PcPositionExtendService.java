package com.hp.sh.expv3.pc.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionStatVo;

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

    /**
     * 查询收益(已实现盈亏)
     * select sum(pnl) from pc_order_trade where pos_id = ? and asset = ? and user_id = ?
     *
     * @param userId
     * @param asset
     * @param posId  仓位ID
     * @return
     */
    BigDecimal getPl(Long userId, String asset, Long posId);

    /**
     * 查询收益率
     * getPl/pos.initMargin
     * ? = ?
     *
     * @param userId
     * @param asset
     * @param posId  仓位ID
     * @return
     */
    BigDecimal getPlRatio(Long userId, String asset, Long posId);

    PageResult<PcPositionVo> pageQueryPositionList(Long userId, String asset, String symbol, Long posId, Integer liqStatus, Integer pageNo, Integer pageSize);

    List<PcPositionVo> findActivePosition(Long userId, String asset, String symbol);

    //通过用户id，资产，交易对查询均价
    BigDecimal getAvgPrice(Long userId, String asset, String symbol);

    List<PcPositionVo> selectPosByAccount(Long userId, String asset, String symbol, Long startTime);

    List<PcSymbolPositionStatVo> getSymbolPositionStat(String asset, String symbol);
}
