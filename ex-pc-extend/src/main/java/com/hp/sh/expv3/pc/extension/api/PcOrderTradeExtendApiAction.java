package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.util.CommonDateUtils;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeExtendVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.PcTradeVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@RestController
public class PcOrderTradeExtendApiAction implements PcOrderTradeExtendApi {
    private static final Logger logger = LoggerFactory.getLogger(PcOrderTradeExtendApiAction.class);


    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;

    @Override
    public List<PcOrderTradeDetailVo> queryOrderTradeDetail(Long userId, String asset, String symbol, String orderId, String startTime, String endTime) {
        logger.info("进人查询当前委托的交易记录接口，参数为：userId={},asset={},symbol={},orderId={},startTime={},endTime={}", userId, asset, symbol, orderId, startTime, endTime);
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId || StringUtils.isEmpty(orderId)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.queryOrderTrade(userId, asset, symbol, orderId, CommonDateUtils.stringToTimestamp(startTime), CommonDateUtils.stringToTimestamp(endTime));
        //封装结果集
        this.toResult(result, voList);

        return result;
    }


    /**
     * 查询成交记录
     *
     * @param asset     资产
     * @param symbol    交易对
     * @param gtTradeId 请求大于trade_id的数据
     * @param ltTradeId 请求小于trade_id的数据
     * @param count     返回条数
     * @return
     */
    @Override
    public List<PcOrderTradeDetailVo> queryTradeRecord(String asset, String symbol, Long gtTradeId, Long ltTradeId, Integer count, String startTime, String endTime) {
        logger.info("进人查询成交记录接口，参数为：asset={},symbol={},gtTradeId={},ltTradeId={},count={},startTime={},endTime={}", asset, symbol, gtTradeId, ltTradeId, count, startTime, endTime);
        checkParam(asset, symbol, count);
        //如果同时传了gtTradeId和ltTradeId 则以gtOrderId为查询条件，同时不传，则查全部
        if (gtTradeId != null && ltTradeId != null) {
            ltTradeId = null;
        }
        String[] startAndEndTime = getStartAndEndTime(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<String> assetList = Arrays.asList(asset.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<String> symbolList = Arrays.asList(symbol.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<PcOrderTradeVo> voList = pcOrderTradeService.queryTradeRecords(assetList, symbolList, gtTradeId, ltTradeId, count, startTime, endTime);
        //封装结果集
        this.toResult(result, voList);

        return result;
    }


    /**
     * 查小于某个时间点的最大的一条记录
     *
     * @param asset    资产
     * @param symbol   合约交易品种
     * @param statTime 成交时间
     * @return
     */
    @Override
    public PcOrderTradeDetailVo selectLessTimeTrade(String asset, String symbol, Long statTime) {
        logger.info("进人查小于某个时间点的最大的一条记录接口，参数为：asset={},symbol={},statTime={}", asset, symbol, statTime);

        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || statTime == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        PcOrderTradeVo vo = pcOrderTradeService.selectLessTimeTrade(asset, symbol, statTime);
        PcOrderTradeDetailVo detailVo = new PcOrderTradeDetailVo();
        if (vo != null) {
            BeanUtils.copyProperties(vo, detailVo);
            detailVo.setAsset(vo.getAsset());
            detailVo.setSymbol(vo.getSymbol());
            detailVo.setQty(vo.getVolume());
            detailVo.setAmt(vo.getPrice().multiply(vo.getVolume()));
        }
        return detailVo;
    }

    /**
     * 查某个用户的所有成交记录
     *
     * @param asset  资产
     * @param symbol 合约交易品种
     * @param userId 用户id
     * @return
     */
    @Override
    public List<PcOrderTradeDetailVo> selectAllTradeListByUser(String asset, String symbol, Long userId) {
        if (userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.selectAllTradeListByUser(asset, symbol, userId);
        this.toResult(result, voList);
        return result;
    }

    @Override
    public List<PcOrderTradeExtendVo> selectTradeListByUserId(String asset, String symbol, Long userId, Long startTime, Long endTime) {
        logger.info("进人查某个时间区间某个用户的成交记录接口，参数为：asset={},symbol={},userId={},startTime={},endTime={}", asset, symbol, userId, startTime, endTime);

        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        if (startTime == null) {
            String s = CommonDateUtils.timestampToString(startTime);
            startTime = CommonDateUtils.stringToTimestamp(s);
        }
        if (endTime == null) {
            endTime = Instant.now().toEpochMilli();
        }

        List<PcOrderTradeExtendVo> pcTradeVo = pcOrderTradeService.selectTradeListByUserId(asset, symbol, startTime, endTime, userId);
        return pcTradeVo;
    }

    @Override
    public BigDecimal queryPcTradeFee(Long userId, String asset, Integer makerFlag, Long beginTime, Long endTime) {

        if (StringUtils.isEmpty(asset) || userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }


        return pcOrderTradeService.queryPcTradeFee(userId, asset, makerFlag, beginTime, endTime);
    }

    @Override
    public List<PcOrderTradeDetailVo> selectPcFeeCollectByAccountId(String asset, String symbol, Long userId, Long statTime, Long endTime) {
        logger.info("进人查询成交记录列表(后台admin接口)，参数为：asset={},symbol={},userId={},statTime={},endTime={}", asset, symbol, userId, statTime, endTime);

        if (userId == null || statTime == null || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.selectPcFeeCollectByAccountId(asset, symbol, userId, statTime, endTime);
        this.toResult(result, voList);
        return result;
    }

    private String getDefaultDateTime(String startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        //如果开始时间，结束时间没有值则给默认今天时间
        if (org.springframework.util.StringUtils.isEmpty(startTime)) {
            startTime = formatter.format(dateTime);
        }
        return startTime;
    }

    private String[] getStartAndEndTime(String startTime, String endTime) {
        if (StringUtils.isEmpty(startTime)) {
            startTime = getDefaultDateTime(startTime);
            String[] split = startTime.split("-");
            int day = Integer.parseInt(split[2]) + 1;
            endTime = split[0] + "-" + split[1] + "-" + day;
        }
        String[] startAndEndTime = {startTime, endTime};
        return startAndEndTime;
    }

    //封装结果集
    private void toResult(List<PcOrderTradeDetailVo> result, List<PcOrderTradeVo> voList) {
        if (!CollectionUtils.isEmpty(voList)) {
            for (PcOrderTradeVo pcOrderTradeVo : voList) {
                PcOrderTradeDetailVo detailVo = new PcOrderTradeDetailVo();
                BeanUtils.copyProperties(pcOrderTradeVo, detailVo);
                detailVo.setFee(detailVo.getFee().negate());
                detailVo.setAsset(pcOrderTradeVo.getAsset());
                detailVo.setSymbol(pcOrderTradeVo.getSymbol());
                detailVo.setQty(pcOrderTradeVo.getVolume());
                detailVo.setAmt(pcOrderTradeVo.getPrice().multiply(pcOrderTradeVo.getVolume()));
                result.add(detailVo);
            }
        }
    }

    private void checkParam(String asset, String symbol, Integer count) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || count == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        if (count > 100) {
            throw new ExException(PcCommonErrorCode.MORE_THAN_MAX_ROW);
        }

    }

}
