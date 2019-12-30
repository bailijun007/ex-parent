package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.extension.service.PcAccountExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.CurrentPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.strategy.PositionStrategyContext;

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
    private PcOrderTradeExtendService pcOrderTradeService;

    @Autowired
    private PositionStrategyContext positionStrategyContext;

    @Autowired
    private MarkPriceService markPriceService;

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
                BigDecimal realisedPnl = pcOrderTradeService.getRealisedPnl(positionVo.getId(), positionVo.getUserId());
                List<PcOrderVo> pcOrderVos = pcOrderExtendService.orderList(positionVo.getId(), positionVo.getUserId());
                BigDecimal volume = pcOrderVos.stream().map(PcOrderVo::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
                CurrentPositionVo currentPositionVo = new CurrentPositionVo();
                BeanUtils.copyProperties(positionVo, currentPositionVo);
                currentPositionVo.setUserId(positionVo.getUserId());
                currentPositionVo.setRealisedPnl(realisedPnl);
                //可平数量=this.volume -volume
                currentPositionVo.setAvailQty(positionVo.getVolume().subtract(volume));
                currentPositionVo.setMarginMode(positionVo.getMarginMode());
                currentPositionVo.setEntryPrice(positionVo.getMeanPrice());
                currentPositionVo.setLiquidationPrice(positionVo.getLiqPrice());
                currentPositionVo.setMaintMarginRatio(positionVo.getHoldMarginRatio());
                currentPositionVo.setQty(positionVo.getVolume());
                currentPositionVo.setBidFlag(positionVo.getLongFlag());
                currentPositionVo.setAutoIncreaseFlag(positionVo.getAutoAddFlag());
                currentPositionVo.setPosPnlRatio(realisedPnl.divide(positionVo.getInitMargin()));
                currentPositionVo.setCtime(positionVo.getCreated());

                HoldPosStrategy ps = positionStrategyContext.getHoldPosStrategy(positionVo.getAsset(), positionVo.getSymbol());
                BigDecimal markPrice = markPriceService.getCurrentMarkPrice(positionVo.getAsset(), positionVo.getSymbol());
                //未实现盈亏 掉老王接口
                BigDecimal pnl = ps.calcPnl(positionVo.getLongFlag(), positionVo.getVolume().multiply(positionVo.getFaceValue()), positionVo.getMeanPrice(), markPrice);
                currentPositionVo.setPnl(pnl);

                BigDecimal posMarginRatio = ps.calPosMarginRatio(positionVo.getPosMargin(), pnl, positionVo.getFaceValue(), positionVo.getVolume(), markPrice);
                currentPositionVo.setPosMarginRatio(posMarginRatio);

                result.add(currentPositionVo);
            }
        }


        return result;
    }
}
