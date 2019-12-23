package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.service.PcAccountExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.CurrentPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 永续合约_仓位 扩展接口
 *
 * @author BaiLiJun  on 2019/12/23
 */
@RestController
public class PcPositionExtendApiAction implements PcPositionExtendApi {
    @Autowired
    private PcAccountExtendService pcAccountExtendService;

    @Autowired
    private PcOrderExtendService pcOrderExtendService;

    @Autowired
    private PcPositionExtendService pcPositionExtendService;

    @Autowired
    private PcOrderTradeService pcOrderTradeService;

    /*
     * @param userId
     * @param asset
     * @param symbol
     * @return
     */
    @Override
    public List<CurrentPositionVo> findCurrentPosition(Long userId, String asset, String symbol) {
        List<CurrentPositionVo> result = new ArrayList<>();
        List<PcPositionVo> list = pcPositionExtendService.findCurrentPosition(userId, asset, symbol);
        if (!CollectionUtils.isEmpty(list)) {
            for (PcPositionVo positionVo : list) {
                //已实现盈亏
                BigDecimal realisedPnl = pcOrderTradeService.getRealisedPnlByPosIdAndUserId(positionVo.getId(), positionVo.getUserId());
                List<PcOrderVo> pcOrderVos = pcOrderExtendService.orderList(positionVo.getId(), positionVo.getUserId());
                BigDecimal volume = pcOrderVos.stream().map(PcOrderVo::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
                CurrentPositionVo currentPositionVo = new CurrentPositionVo();
                BeanUtils.copyProperties(positionVo, currentPositionVo);
                currentPositionVo.setRealisedPnl(realisedPnl);
                //可平数量=this.volume -volume
                currentPositionVo.setAvailQty(positionVo.getVolume().subtract(volume));
                currentPositionVo.setMarginMode(positionVo.getMarginMode());
                currentPositionVo.setEntryPrice(positionVo.getMeanPrice());
                currentPositionVo.setLiquidationPrice(positionVo.getLiqPrice());
                currentPositionVo.setMaintMarginRatio(positionVo.getHoldRatio());
                currentPositionVo.setQty(positionVo.getVolume());
                currentPositionVo.setBidFlag(positionVo.getLongFlag());
                currentPositionVo.setAutoIncreaseFlag(positionVo.getAutoAddFlag());
                currentPositionVo.setPosPnlRatio(realisedPnl.divide(positionVo.getInitMargin()));
                Date created = positionVo.getCreated();
                if (created != null) {
                    currentPositionVo.setCtime(created.getTime());
                }

                //保证金率 表示5% (${pos_margin} - ${未实现盈亏} ) / ( faceValue * ${this.volume} / ${当前标记价格})

                //未实现盈亏 掉老王接口

                result.add(currentPositionVo);
            }
        }


        return result;
    }
}
