package com.hp.sh.expv3.pc.extension.api;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.hp.sh.expv3.pc.extension.util.CommonDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

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
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionStatVo;
import com.hp.sh.expv3.pc.extension.vo.PcSymbolPositionTotalVo;
import com.hp.sh.expv3.pc.strategy.HoldPosStrategy;
import com.hp.sh.expv3.pc.strategy.PcStrategyContext;
import com.hp.sh.expv3.utils.math.Precision;

/**
 * 永续合约_仓位 扩展接口
 *
 * @author BaiLiJun  on 2019/12/23
 */
@RestController
public class PcPositionExtendApiAction implements PcPositionExtendApi {
    private static final Logger logger = LoggerFactory.getLogger(PcPositionExtendApiAction.class);

    @Autowired
    private PcAccountExtendService pcAccountExtendService;

    @Autowired
    private PcOrderExtendService pcOrderExtendService;

    @Autowired
    private PcPositionExtendService pcPositionExtendService;

    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;

    @Autowired
    private PcStrategyContext pcStrategyContext;

    @Autowired
    private MarkPriceService markPriceService;

    /*
     * @param userId
     * @param asset
     * @param symbol
     * @return
     */
    @Override
    public List<CurrentPositionVo> findCurrentPosition(Long userId, String asset, String symbol, String startTime, String endTime) {
        logger.info("进入查询当前活动仓位接口，收到的参数为：userId={},asset={},symbol={},startTime={},endTime={}", userId, asset, symbol, startTime, endTime);
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        String[] startAndEndTime = CommonDateUtils.getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];

        List<CurrentPositionVo> result = new ArrayList<>();
        List<PcPositionVo> list = pcPositionExtendService.findActivePosition(userId, asset, symbol, startTime, endTime);
        this.convertPositionList(result, list);

        return result;
    }

    @Override
    public PageResult<CurrentPositionVo> findPositionList(Long userId, String asset, Long posId, Integer liqStatus, String symbol, Integer pageNo, Integer pageSize, String startTime, String endTime) {
        logger.info("进入查询仓位列表接口，收到的参数为：userId={},asset={},symbol={},posId={},liqStatus={},pageNo={},pageSize={},startTime={},endTime={}", userId, asset, symbol, posId, liqStatus, pageNo, pageSize, startTime, endTime);

        if (pageNo == null || pageSize == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        String[] startAndEndTime = CommonDateUtils.getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        PageResult<CurrentPositionVo> result = new PageResult<>();
        List<CurrentPositionVo> list = new ArrayList<>();
        PageResult<PcPositionVo> voList = pcPositionExtendService.pageQueryPositionList(userId, asset, symbol, posId, liqStatus, pageNo, pageSize, startTime, endTime);
        result.setPageNo(voList.getPageNo());
        result.setPageCount(voList.getPageCount());
        result.setRowTotal(voList.getRowTotal());
        this.convertPositionList(list, voList.getList());

        result.setList(list);
        return result;
    }

    @Override
    public List<CurrentPositionVo> selectPosByAccount(Long userId, String asset, String symbol, Long startTime) {
        logger.info("进入查询已平仓位信息列表接口，收到的参数为：userId={},asset={},symbol={},startTime={}", userId, asset, symbol, startTime);
        if (userId == null || startTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        LocalDateTime localDateTime = LocalDateTime.now();
        Long endTime = CommonDateUtils.localDateTimeToTimestamp(localDateTime);
        List<CurrentPositionVo> result = new ArrayList<>();
        List<PcPositionVo> list = pcPositionExtendService.selectPosByAccount(userId, asset, symbol, startTime, endTime);
        this.convertPositionList(result, list);

        return result;
    }


    @Override
    public List<PcSymbolPositionStatVo> getSymbolPositionStat(String asset, String symbol) {
        List<PcSymbolPositionStatVo> list = pcPositionExtendService.getSymbolPositionStat(asset, symbol);
        return list;
    }

    @Override
    public PcSymbolPositionTotalVo getSymbolPositionTotal(String asset, String symbol) {
        logger.info("进入查询合约持仓量总数接口，收到的参数为：asset={},symbol={}", asset, symbol);
        return pcPositionExtendService.getSymbolPositionTotal(asset, symbol);
    }

    private void convertPositionList(List<CurrentPositionVo> result, List<PcPositionVo> list) {
        if (!CollectionUtils.isEmpty(list)) {
            for (PcPositionVo positionVo : list) {
                //已实现盈亏
                BigDecimal realisedPnl2 = positionVo.getRealisedPnl();
                BigDecimal realisedPnl = pcOrderTradeService.getRealisedPnl(positionVo.getId(), positionVo.getUserId(), null);
                List<PcOrderVo> pcOrderVos = pcOrderExtendService.activeOrderList(positionVo.getId(), positionVo.getUserId());
                BigDecimal volume = pcOrderVos.stream().map(order -> order.getVolume().subtract(order.getFilledVolume())).reduce(BigDecimal.ZERO, BigDecimal::add);
                CurrentPositionVo currentPositionVo = new CurrentPositionVo();
                BeanUtils.copyProperties(positionVo, currentPositionVo);
                currentPositionVo.setAccuVolume(positionVo.getAccuVolume());
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
                currentPositionVo.setPosPnlRatio(realisedPnl.divide(positionVo.getInitMargin(), Precision.PERCENT_PRECISION, Precision.LESS));
                currentPositionVo.setCtime(positionVo.getCreated());

                BigDecimal markPrice = markPriceService.getCurrentMarkPrice(positionVo.getAsset(), positionVo.getSymbol());
                //未实现盈亏
                BigDecimal pnl = pcStrategyContext.calcFloatingPnl(positionVo);
                currentPositionVo.setPnl(pnl);

                if (positionVo.getVolume().compareTo(BigDecimal.ZERO) != 0 && positionVo.getFaceValue().compareTo(BigDecimal.ZERO) != 0) {
                    BigDecimal posMarginRatio = pcStrategyContext.calPosMarginRatio(positionVo, pnl, markPrice);
                    currentPositionVo.setPosMarginRatio(posMarginRatio);
                }

                result.add(currentPositionVo);
            }
        }
    }
}
