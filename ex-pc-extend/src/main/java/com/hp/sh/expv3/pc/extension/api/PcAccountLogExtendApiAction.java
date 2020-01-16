package com.hp.sh.expv3.pc.extension.api;

import com.alibaba.fastjson.JSON;
import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.constant.RedisKey;
import com.hp.sh.expv3.pc.extension.constant.ExtCommonConstant;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.*;
import com.hp.sh.expv3.pc.extension.vo.*;
import com.hp.sh.expv3.pc.msg.PcAccountLog;
import com.hp.sh.expv3.utils.math.Precision;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun on 2019/12/25
 */
@RestController
public class PcAccountLogExtendApiAction implements PcAccountLogExtendApi {
    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Resource(name = "templateDB5")
    private StringRedisTemplate templateDB5;

    @Autowired
    private PcAccountRecordExtendService pcAccountRecordExtendService;

    @Autowired
    private PcAccountLogExtendService pcAccountLogExtendService;

    @Autowired
    private PcLiqRecordExtendService pcLiqRecordService;

    @Autowired
    private PcOrderTradeExtendService pcOrderTradeExtendService;

    @Autowired
    private PcOrderExtendService pcOrderExtendService;

    /**
     * 查询pc永续合约账户
     *
     * @param userId      用户id
     * @param asset       资产
     * @param tradeType   类型
     *                    0.全部,1.成交开多,2.成交开空,3.成交平多,4.成交平空,5.转入,6.转出,7.手动追加保证金,8.减少保证金,9
     *                    .自动追加保证金,10.调低杠杆追加保证金,11.强平平多,12强平平空
     * @param historyType 1.最近两天,2.两天到三个月
     * @param startDate   开始时间(当history_type是2时,填写)
     * @param endDate     结束时间 (当history_type是2时,填写)
     * @param pageNo      当前页
     * @param pageSize    页大小
     * @param symbol      交易对
     * @return
     */
    @Override
    public PageResult<PcAccountRecordLogVo> query(Long userId, String asset, Integer tradeType, Integer historyType,
                                                  Long startDate, Long endDate, Integer pageNo, Integer pageSize, String symbol) {
        this.checkParam(userId, asset, tradeType, historyType, startDate, endDate, pageNo, pageSize, symbol);

        // 获取面值
        BigDecimal faceValue = this.getFaceValue(asset, symbol);

        PageResult<PcAccountRecordLogVo> result = new PageResult<PcAccountRecordLogVo>();

        PageResult<PcAccountLogVo> pcAccountLogList = pcAccountLogExtendService.pageQueryPcAccountLogList(userId, asset,
                tradeType, historyType, startDate, endDate, symbol, pageNo, pageSize);
        result.setPageNo(pcAccountLogList.getPageNo());
        result.setPageCount(pcAccountLogList.getPageCount());
        result.setRowTotal(pcAccountLogList.getRowTotal());
        List<PcAccountRecordLogVo> recordLogs = new ArrayList<>(pcAccountLogList.getList().size());
        result.setList(recordLogs);

        if (!CollectionUtils.isEmpty(pcAccountLogList.getList())) {
            for (PcAccountLogVo pcAccountLogVo : pcAccountLogList.getList()) {
                PcAccountRecordLogVo recordLogVo = new PcAccountRecordLogVo();
                BeanUtils.copyProperties(pcAccountLogVo, recordLogVo);
                recordLogVo.setTradeType(pcAccountLogVo.getType());
                recordLogs.add(recordLogVo);
            }

            Map<Integer, List<PcAccountRecordLogVo>> type2Refs = new HashMap<>(12);
            for (PcAccountRecordLogVo pcAccountVo : recordLogs) {
                List<PcAccountRecordLogVo> refs = type2Refs.get(pcAccountVo.getTradeType());
                if (null == refs) {
                    refs = new ArrayList<>();
                    type2Refs.put(pcAccountVo.getTradeType(), refs);
                }
                refs.add(pcAccountVo);
            }

            if (type2Refs.containsKey(PcAccountLog.TYPE_LIQ_LONG)
                    || type2Refs.containsKey(PcAccountLog.TYPE_LIQ_SHORT)) {
                List<PcAccountRecordLogVo> refs = new ArrayList<>();
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_LIQ_LONG, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_LIQ_SHORT, Collections.emptyList()));
                appendPcLiqRecordData(faceValue, asset, symbol, userId, refs);
            }
            if (type2Refs.containsKey(PcAccountLog.TYPE_ADD_TO_MARGIN)
                    || type2Refs.containsKey(PcAccountLog.TYPE_REDUCE_MARGIN)
                    || type2Refs.containsKey(PcAccountLog.TYPE_AUTO_ADD_MARGIN)
                    || type2Refs.containsKey(PcAccountLog.TYPE_LEVERAGE_ADD_MARGIN)
                    || type2Refs.containsKey(PcAccountLog.TYPE_FUND_TO_PC)
                    || type2Refs.containsKey(PcAccountLog.TYPE_PC_TO_FUND)) {
                List<PcAccountRecordLogVo> refs = new ArrayList<>();
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_ADD_TO_MARGIN, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_REDUCE_MARGIN, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_AUTO_ADD_MARGIN, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_LEVERAGE_ADD_MARGIN, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_FUND_TO_PC, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_PC_TO_FUND, Collections.emptyList()));
                appendTransferMarginData(faceValue, asset, symbol, userId, refs);
            }
            if (type2Refs.containsKey(PcAccountLog.TYPE_TRAD_OPEN_LONG)
                    || type2Refs.containsKey(PcAccountLog.TYPE_TRAD_OPEN_SHORT)
                    || type2Refs.containsKey(PcAccountLog.TYPE_TRAD_CLOSE_LONG)
                    || type2Refs.containsKey(PcAccountLog.TYPE_TRAD_CLOSE_SHORT)) {
                List<PcAccountRecordLogVo> refs = new ArrayList<>();
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_TRAD_OPEN_LONG, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_TRAD_OPEN_SHORT, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_TRAD_CLOSE_LONG, Collections.emptyList()));
                refs.addAll(type2Refs.getOrDefault(PcAccountLog.TYPE_TRAD_CLOSE_SHORT, Collections.emptyList()));
                appendOrderTradeData(faceValue, asset, symbol, userId, refs);
            }

        }

        return result;
    }

    /**
     * @param tradeType
     * @param pageNo
     * @param pageSize
     * @param result
     * @param list
     */
    private void rePageCut(Integer tradeType, Integer pageNo, Integer pageSize, PageResult<PcAccountRecordLogVo> result,
                           List<PcAccountRecordLogVo> list) {
        if (ExtCommonConstant.TRADE_TYPE_ALL.equals(tradeType)
                || ExtCommonConstant.TRADE_TYPE_DEPOSITE_AND_WITHDRAW.equals(tradeType)
                || ExtCommonConstant.TRADE_TYPE_MAKE_ORDER.equals(tradeType)
                || ExtCommonConstant.TRADE_TYPE_CHANGE_MARGIN.equals(tradeType)
                || ExtCommonConstant.TRADE_TYPE_LIQ.equals(tradeType)
                || ExtCommonConstant.TRADE_TYPE_TRANSFER.equals(tradeType)) {
            List<PcAccountRecordLogVo> logVoList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize)
                    .collect(Collectors.toList());
            result.setList(logVoList);
            Integer rowTotal = list.size();
            result.setPageNo(pageNo);
            result.setRowTotal(new Long(rowTotal + ""));
            result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        }
    }

    private void checkParam(Long userId, String asset, Integer tradeType, Integer historyType, Long startDate,
                            Long endDate, Integer pageNo, Integer pageSize, String symbol) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || tradeType == null || null == userId
                || historyType == null || pageNo == null || pageSize == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        if (ExtCommonConstant.HISTORY_TYPE_LAST_THREE_MONTHS.equals(historyType)) {
            if (startDate == null && endDate == null) {
                throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
            }
        }
    }

    // 封装开平仓数据
    @Deprecated
    private void getOrderTradeData(BigDecimal faceValue, PcAccountLogVo pcAccountLogVo,
                                   PcAccountRecordLogVo recordLogVo) {
        PcOrderTradeVo pcOrderTradeVo = pcOrderTradeExtendService.getPcOrderTrade(pcAccountLogVo.getRefId(),
                pcAccountLogVo.getAsset(), pcAccountLogVo.getSymbol(), pcAccountLogVo.getUserId(),
                pcAccountLogVo.getTime());
        if (null != pcOrderTradeVo) {
            recordLogVo.setTradeAmt(pcOrderTradeVo.getVolume());
            recordLogVo.setNoTradeAmt(pcOrderTradeVo.getRemainVolume());
            recordLogVo.setVolume(pcOrderTradeVo.getPnl());
            recordLogVo.setTradePrice(pcOrderTradeVo.getPrice());
            recordLogVo.setFeeRatio(pcOrderTradeVo.getFeeRatio());
            recordLogVo.setFee(pcOrderTradeVo.getFee());
            recordLogVo.setOrderId(pcOrderTradeVo.getOrderId());
            Long orderId = pcOrderTradeVo.getOrderId();
            String asset = pcAccountLogVo.getAsset();
            String symbol = pcAccountLogVo.getSymbol();
            Long userId = pcAccountLogVo.getUserId();
            PcOrderVo pcOrderVo = pcOrderExtendService.getPcOrder(orderId, asset, symbol, userId);
            Optional<PcOrderVo> orderVoOptional = Optional.ofNullable(pcOrderVo);
            recordLogVo.setOrderAmt(orderVoOptional.map(o -> o.getVolume()).orElse(BigDecimal.ZERO));
            recordLogVo.setOrderType(orderVoOptional.map(o -> o.getOrderType()).orElse(null));
            recordLogVo.setOrderPrice(orderVoOptional.map(o -> o.getPrice()).orElse(null));
        }
    }

    private void appendOrderTradeData(BigDecimal faceValue, String asset, String symbol, Long userId,
                                      List<PcAccountRecordLogVo> recordLogVos) {
        List<Long> refIds = recordLogVos.stream().map(PcAccountRecordLogVo::getRefId).collect(Collectors.toList());
        List<PcOrderTradeVo> pcLiqRecordVo = pcOrderTradeExtendService.listPcOrderTrade(refIds, asset, symbol, userId);
        if (null != pcLiqRecordVo) {
            Map<Long, PcOrderTradeVo> id2Vo = pcLiqRecordVo.stream()
                    .collect(Collectors.toMap(PcOrderTradeVo::getId, Function.identity()));
            for (PcAccountRecordLogVo recordLogVo : recordLogVos) {
                Optional<PcOrderTradeVo> pcOrderTradeVo = Optional.ofNullable(id2Vo.get(recordLogVo.getRefId()));

                recordLogVo.setTradeAmt(pcOrderTradeVo.map(PcOrderTradeVo::getVolume).orElse(BigDecimal.ZERO));
                recordLogVo.setNoTradeAmt(pcOrderTradeVo.map(PcOrderTradeVo::getRemainVolume).orElse(BigDecimal.ZERO));
                recordLogVo.setVolume(pcOrderTradeVo.map(PcOrderTradeVo::getPnl).orElse(BigDecimal.ZERO));
                recordLogVo.setTradePrice(pcOrderTradeVo.map(PcOrderTradeVo::getPrice).orElse(BigDecimal.ZERO));
                recordLogVo.setFeeRatio(pcOrderTradeVo.map(PcOrderTradeVo::getFeeRatio).orElse(BigDecimal.ZERO));
                recordLogVo.setFee(pcOrderTradeVo.map(pt->pt.getFee().negate()).orElse(BigDecimal.ZERO));
                Long orderId = pcOrderTradeVo.map(PcOrderTradeVo::getOrderId).orElse(null);
                recordLogVo.setOrderId(orderId);
                PcOrderVo pcOrderVo = pcOrderExtendService.getPcOrder(orderId, asset, symbol, userId);
                Optional<PcOrderVo> orderVoOptional = Optional.ofNullable(pcOrderVo);
                recordLogVo.setOrderAmt(orderVoOptional.map(o -> o.getVolume()).orElse(BigDecimal.ZERO));
                recordLogVo.setOrderType(orderVoOptional.map(o -> o.getOrderType()).orElse(null));
                recordLogVo.setOrderPrice(orderVoOptional.map(o -> o.getPrice()).orElse(null));
            }
        }
    }

    // 封装追加/减少保证金数据
    @Deprecated
    private void getAddOrReduceMarginData(BigDecimal faceValue, PcAccountLogVo pcAccountLogVo,
                                          PcAccountRecordLogVo recordLogVo) {
        packagingPcAccountRecordResult(pcAccountLogVo, recordLogVo);
    }

    private void packagingPcAccountRecordResult(PcAccountLogVo pcAccountLogVo, PcAccountRecordLogVo recordLogVo) {
        PcAccountRecordVo pcAccountRecordVo = pcAccountRecordExtendService.getPcAccountRecord(pcAccountLogVo.getRefId(),
                pcAccountLogVo.getAsset(), pcAccountLogVo.getUserId(), pcAccountLogVo.getTime());
        Optional<PcAccountRecordVo> optional = Optional.ofNullable(pcAccountRecordVo);
        recordLogVo.setVolume(optional.map(o -> o.getAmount()).orElse(BigDecimal.ZERO));
        recordLogVo.setFee(BigDecimal.ZERO);
    }

    // 封装转入转出数据
    @Deprecated
    private void getPcAccountRecordData(BigDecimal faceValue, PcAccountLogVo pcAccountLogVo,
                                        PcAccountRecordLogVo recordLogVo) {
        packagingPcAccountRecordResult(pcAccountLogVo, recordLogVo);
    }

    private void appendTransferMarginData(BigDecimal faceValue, String asset, String symbol, Long userId,
                                          List<PcAccountRecordLogVo> recordLogVos) {
        List<Long> refIds = recordLogVos.stream().map(PcAccountRecordLogVo::getRefId).collect(Collectors.toList());
        // TODO xb，这里有风险：资金转入转出没问题，但是保证金是和symbol相关的，这里没有用到
        List<PcAccountRecordVo> pcLiqRecordVo = pcAccountRecordExtendService.listPcAccountRecord(refIds, asset, userId);
        if (null != pcLiqRecordVo) {
             Map<Long, PcAccountRecordVo> id2Vo = pcLiqRecordVo.stream()
                    .collect(Collectors.toMap(PcAccountRecordVo::getId, Function.identity()));
            for (PcAccountRecordLogVo recordLogVo : recordLogVos) {
                Optional<PcAccountRecordVo> recordVoOptional = Optional.ofNullable(id2Vo.get(recordLogVo.getRefId()));
                recordLogVo.setFee(BigDecimal.ZERO);
                if(recordLogVo.getTradeType()==PcAccountLog.TYPE_REDUCE_MARGIN){
                    recordLogVo.setVolume(recordVoOptional.map(o -> o.getAmount().negate()).orElse(BigDecimal.ZERO));
                }else {
                    recordLogVo.setVolume(recordVoOptional.map(o -> o.getAmount()).orElse(BigDecimal.ZERO));
                }
            }
        }
    }

    /**
     * 封装强平数据
     *
     * @param faceValue    面值
     * @param recordLogVos 最终返回结果集
     */
    private void appendPcLiqRecordData(BigDecimal faceValue, String asset, String symbol, Long userId,
                                       List<PcAccountRecordLogVo> recordLogVos) {
        List<Long> refIds = recordLogVos.stream().map(PcAccountRecordLogVo::getRefId).collect(Collectors.toList());
        List<PcLiqRecordVo> pcLiqRecordVo = pcLiqRecordService.listPcLiqRecord(asset, symbol, userId, refIds);
        if (null != pcLiqRecordVo) {
            final Map<Long, PcLiqRecordVo> id2Vo = pcLiqRecordVo.stream()
                    .collect(Collectors.toMap(PcLiqRecordVo::getId, Function.identity()));
            for (PcAccountRecordLogVo recordLogVo : recordLogVos) {
                Optional<PcLiqRecordVo> recordVoOptional = Optional.ofNullable(id2Vo.get(recordLogVo.getRefId()));

                BigDecimal volume = recordVoOptional.map(r -> r.getVolume()).orElse(BigDecimal.ZERO);
                BigDecimal liqPrice = recordVoOptional.map(r -> r.getLiqPrice()).orElse(BigDecimal.ZERO);
                BigDecimal meanPrice = recordVoOptional.map(r -> r.getMeanPrice()).orElse(BigDecimal.ZERO);
                BigDecimal fee = recordVoOptional.map(r -> r.getFee()).orElse(BigDecimal.ZERO);
                BigDecimal feeRatio = recordVoOptional.map(r -> r.getFeeRatio()).orElse(BigDecimal.ZERO);
                recordLogVo.setTradeAmt(volume.multiply(faceValue));
                recordLogVo.setOrderAmt(volume.multiply(faceValue));
                recordLogVo.setNoTradeAmt(BigDecimal.ZERO);
                if (BigDecimal.ZERO.compareTo(meanPrice) == 0) {
                    recordLogVo.setVolume(BigDecimal.ZERO);
                } else {
                    recordLogVo.setVolume(volume.multiply(faceValue)
                            .divide(meanPrice, Precision.PERCENT_PRECISION, Precision.LESS).negate());
                }
                recordLogVo.setTradePrice(liqPrice);
                recordLogVo.setFee(fee.negate());
                recordLogVo.setFeeRatio(feeRatio);
                recordLogVo.setOrderPrice(liqPrice);
            }
        }
    }

    /**
     * 获取面值
     *
     * @param asset  资产
     * @param symbol 交易对
     * @return
     */
    public BigDecimal getFaceValue(String asset, String symbol) {
        PcContractVO vo = this.getPcContract(asset, symbol);
        Optional<PcContractVO> optional = Optional.ofNullable(vo);
        BigDecimal decimal = optional.map(p -> p.getFaceValue()).orElse(BigDecimal.ZERO);
        return decimal;
    }

    private PcContractVO getPcContract(String asset, String symbol) {
        HashOperations hashOperations = templateDB0.opsForHash();
        String hashKey = asset + "__" + symbol;
        Object o = hashOperations.get(RedisKey.PC_CONTRACT, hashKey);
        String json = o.toString();
        PcContractVO vo = JSON.parseObject(json, PcContractVO.class);
        return vo;
    }

}
