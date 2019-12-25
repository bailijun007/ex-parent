package com.hp.sh.expv3.pc.extension.api;

import com.alibaba.fastjson.JSON;
import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.constant.RedisKey;
import com.hp.sh.expv3.pc.extension.constant.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.*;
import com.hp.sh.expv3.pc.extension.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/25
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

    @Override
    public PageResult<PcAccountRecordLogVo> findContractAccountList(Long userId, String asset, Integer tradeType, Integer historyType, String startDate, String endDate, Integer pageNo, Integer pageSize, String symbol) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || tradeType == null || null == userId || historyType == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        if (tradeType == 0) {
            tradeType = null;
        }

        BigDecimal faceValue = this.getFaceValue(asset, symbol);

        PageResult<PcAccountRecordLogVo> result = new PageResult<PcAccountRecordLogVo>();
        List<PcAccountRecordLogVo> list = new ArrayList<>();
        List<PcAccountLogVo> pcAccountLogList = pcAccountLogExtendService.getPcAccountLogList(userId, asset, tradeType, historyType, startDate, endDate, symbol);
        if (!CollectionUtils.isEmpty(pcAccountLogList)) {
            for (PcAccountLogVo pcAccountLogVo : pcAccountLogList) {
                PcAccountRecordLogVo recordLogVo = new PcAccountRecordLogVo();
                BeanUtils.copyProperties(pcAccountLogVo, recordLogVo);

                if (tradeType == 11 || tradeType == 12) {
                    //封装强平数据
                    getPcLiqRecordData(faceValue, pcAccountLogVo, recordLogVo);
                }
                if (tradeType == 5 || tradeType == 6) {
                    //封装转入转出数据
                    getPcAccountRecordData(faceValue, pcAccountLogVo, recordLogVo);
                }

                if (tradeType == 7 || tradeType == 8||tradeType == 9 || tradeType == 10) {
                    //封装追加/减少保证金数据
                    getAddOrReduceMarginData(faceValue, pcAccountLogVo, recordLogVo);
                }

                if (tradeType == 0||tradeType == 1 || tradeType == 2||tradeType == 3 || tradeType == 4) {
                    //封装开平仓数据
                    getOrderTradeData(faceValue, pcAccountLogVo, recordLogVo);
                }

                list.add(recordLogVo);
            }

            List<PcAccountRecordLogVo> voList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
            result.setList(voList);
        }
        int rowTotal = pcAccountLogList.size();
        result.setPageNo(pageNo);
        result.setRowTotal(Long.parseLong(rowTotal + ""));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return result;
    }

    //封装开平仓数据
    private void getOrderTradeData(BigDecimal faceValue, PcAccountLogVo pcAccountLogVo, PcAccountRecordLogVo recordLogVo) {
        PcOrderTradeVo pcOrderTradeVo= pcOrderTradeExtendService.getPcOrderTrade(pcAccountLogVo.getRefId(), pcAccountLogVo.getAsset(), pcAccountLogVo.getSymbol(), pcAccountLogVo.getUserId(), pcAccountLogVo.getTime());
        Optional<PcOrderTradeVo> optional = Optional.ofNullable(pcOrderTradeVo);
        recordLogVo.setTradeAmt(optional.map(o->o.getVolume()).orElse(BigDecimal.ZERO));
        recordLogVo.setNoTradeAmt(optional.map(o->o.getRemainVolume()).orElse(BigDecimal.ZERO));
        recordLogVo.setVolume(optional.map(o->o.getPnl()).orElse(BigDecimal.ZERO));
        recordLogVo.setTradePrice(optional.map(o->o.getPrice()).orElse(BigDecimal.ZERO));
        recordLogVo.setFeeRatio(optional.map(o->o.getFeeRatio()).orElse(BigDecimal.ZERO));
        recordLogVo.setFee(optional.map(o->o.getFee()).orElse(BigDecimal.ZERO));
        recordLogVo.setOrderId(optional.map(o->o.getOrderId()).orElse(null));
        PcOrderVo pcOrderVo=  pcOrderExtendService.getPcOrder(pcOrderTradeVo.getOrderId(), pcAccountLogVo.getAsset(), pcAccountLogVo.getSymbol(), pcAccountLogVo.getUserId());
        Optional<PcOrderVo> orderVoOptional = Optional.ofNullable(pcOrderVo);
        recordLogVo.setOrderAmt(orderVoOptional.map(o->o.getVolume()).orElse(BigDecimal.ZERO));
        recordLogVo.setOrderType(orderVoOptional.map(o->o.getOrderType()).orElse(null));
        recordLogVo.setOrderPrice(orderVoOptional.map(o->o.getPrice()).orElse(null));



    }

    //封装追加/减少保证金数据
    private void getAddOrReduceMarginData(BigDecimal faceValue, PcAccountLogVo pcAccountLogVo, PcAccountRecordLogVo recordLogVo) {
        packagingPcAccountRecordResult(pcAccountLogVo, recordLogVo);
    }

    private void packagingPcAccountRecordResult(PcAccountLogVo pcAccountLogVo, PcAccountRecordLogVo recordLogVo) {
        PcAccountRecordVo pcAccountRecordVo = pcAccountRecordExtendService.getPcAccountRecord(pcAccountLogVo.getRefId(), pcAccountLogVo.getAsset(), pcAccountLogVo.getUserId(), pcAccountLogVo.getTime());
        Optional<PcAccountRecordVo> optional = Optional.ofNullable(pcAccountRecordVo);
        recordLogVo.setVolume(optional.map(o -> o.getAmount()).orElse(BigDecimal.ZERO));
        recordLogVo.setFee(BigDecimal.ZERO);
    }

    //封装转入转出数据
    private void getPcAccountRecordData(BigDecimal faceValue, PcAccountLogVo pcAccountLogVo, PcAccountRecordLogVo recordLogVo) {
        packagingPcAccountRecordResult(pcAccountLogVo, recordLogVo);
    }

    //封装强平数据
    private void getPcLiqRecordData(BigDecimal faceValue, PcAccountLogVo pcAccountLogVo, PcAccountRecordLogVo recordLogVo) {
        PcLiqRecordVo pcLiqRecordVo = pcLiqRecordService.getPcLiqRecord(pcAccountLogVo.getRefId(), pcAccountLogVo.getAsset(), pcAccountLogVo.getSymbol(), pcAccountLogVo.getUserId(), pcAccountLogVo.getTime());
        Optional<PcLiqRecordVo> recordVoOptional = Optional.ofNullable(pcLiqRecordVo);
        BigDecimal volume = recordVoOptional.map(r -> r.getVolume()).orElse(BigDecimal.ZERO);
        BigDecimal liqPrice = recordVoOptional.map(r -> r.getLiqPrice()).orElse(BigDecimal.ZERO);
        BigDecimal fee = recordVoOptional.map(r -> r.getFee()).orElse(BigDecimal.ZERO);
        BigDecimal feeRatio = recordVoOptional.map(r -> r.getFeeRatio()).orElse(BigDecimal.ZERO);
        recordLogVo.setTradeAmt(volume);
        recordLogVo.setOrderAmt(volume);
        recordLogVo.setNoTradeAmt(BigDecimal.ZERO);
        recordLogVo.setVolume(pcLiqRecordVo.getVolume().multiply(faceValue).divide(liqPrice));
        recordLogVo.setTradePrice(liqPrice);
        recordLogVo.setFee(fee);
        recordLogVo.setFeeRatio(feeRatio);
        recordLogVo.setOrderPrice(liqPrice);
    }

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
