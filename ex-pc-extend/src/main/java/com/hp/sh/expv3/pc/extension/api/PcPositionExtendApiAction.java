package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.component.MarkPriceService;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcAccountExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.CurrentPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.strategy.PositionStrategyContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<CurrentPositionVo> result = new ArrayList<>();
        List<PcPositionVo> list = pcPositionExtendService.findActivePosition(userId, asset, symbol);
        this.convertPositionList(result, list);

        return result;
    }

    @Override
    public PageResult<CurrentPositionVo> findPositionList(Long userId, String asset, Long posId, Integer liqStatus, String symbol, Integer pageNo, Integer pageSize) {
        if (pageNo == null || pageSize == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        PageResult<CurrentPositionVo> result = new PageResult<>();
        List<CurrentPositionVo> list = new ArrayList<>();
        PageResult<PcPositionVo> voList = pcPositionExtendService.pageQueryPositionList(userId, asset, symbol, posId, liqStatus, pageNo, pageSize);
        result.setPageNo(voList.getPageNo());
        result.setPageCount(voList.getPageCount());
        result.setRowTotal(voList.getRowTotal());
        this.convertPositionList(list, voList.getList());

        result.setList(list);
        return result;
    }

    private void convertPositionList(List<CurrentPositionVo> result, List<PcPositionVo> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (PcPositionVo positionVo : list) {
                //已实现盈亏
                BigDecimal realisedPnl = pcOrderTradeService.getRealisedPnl(positionVo.getId(), positionVo.getUserId());
                List<PcOrderVo> pcOrderVos = pcOrderExtendService.activeOrderList(positionVo.getId(), positionVo.getUserId());
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
                currentPositionVo.setPosPnlRatio(realisedPnl.divide(positionVo.getInitMargin(), 10, RoundingMode.HALF_UP));
                currentPositionVo.setCtime(positionVo.getCreated());

                HoldPosStrategy ps = positionStrategyContext.getHoldPosStrategy(positionVo.getAsset(), positionVo.getSymbol());
                BigDecimal markPrice = markPriceService.getCurrentMarkPrice(positionVo.getAsset(), positionVo.getSymbol());
                //未实现盈亏 掉老王接口
                BigDecimal pnl = ps.calcPnl(positionVo.getLongFlag(), positionVo.getVolume().multiply(positionVo.getFaceValue()), positionVo.getMeanPrice(), markPrice);
                currentPositionVo.setPnl(pnl);

                if (positionVo.getVolume().compareTo(BigDecimal.ZERO) != 0 && positionVo.getFaceValue().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal posMarginRatio = ps.calPosMarginRatio(positionVo.getPosMargin(), pnl, positionVo.getFaceValue(), positionVo.getVolume(), markPrice);
                    currentPositionVo.setPosMarginRatio(posMarginRatio);
                }

                result.add(currentPositionVo);
            }
        }
    }


}
