package com.hp.sh.expv3.bb.extension.api;

import com.alibaba.fastjson.JSON;
import com.hp.sh.expv3.bb.extension.constant.BbAccountLogConst;
import com.hp.sh.expv3.bb.extension.constant.BbExtRedisKey;
import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.service.BbAccountLogExtService;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import sun.awt.Symbol;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/3/24
 */
@RestController
public class BbAccountLogExtAction implements BbAccountLogExtApi {

    @Autowired
    private BbAccountLogExtService bbAccountLogExtService;


    @Autowired
    private BbOrderTradeExtService bbOrderTradeExtService;

    @Autowired
    private BbAccountRecordExtService bbAccountRecordExtService;

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Override
    public List<BbAccountLogExtVo> query(Long userId, String asset, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer nextPage, Long lastId, Integer pageSize) {
        this.checkParam(userId, asset, historyType, tradeType, startDate, endDate, nextPage, pageSize);
        List<BbAccountLogExtVo> list = new ArrayList<>();
        List<String> symbols=buildSymbols(asset);
        if (CollectionUtils.isEmpty(symbols)){
            throw  new ExException(BbExtCommonErrorCode.SYMBOL_DOES_NOT_EXIST);
        }
        List<BbAccountLogExtVo> voList = null;
        if (null == lastId) {
            voList = bbAccountLogExtService.listBbAccountLogs(userId, asset, symbols, historyType, tradeType, startDate, endDate, pageSize);
        } else {
            voList = bbAccountLogExtService.listBbAccountLogsByPage(userId, asset, symbols, historyType, tradeType, lastId, nextPage, startDate, endDate, pageSize);
        }
        if (!CollectionUtils.isEmpty(voList)) {
            Map<Integer, List<BbAccountLogExtVo>> map = voList.stream().collect(Collectors.groupingBy(BbAccountLogExtVo::getTradeType));
            //买入
            if (map.containsKey(BbAccountLogConst.TYPE_BUY_IN)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.TYPE_BUY_IN, Collections.emptyList());
                this.appendBuyInOrSellOutData(logExtVoList);
                list.addAll(logExtVoList);
            }
            //卖出
            if (map.containsKey(BbAccountLogConst.TYPE_SELL_OUT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.TYPE_SELL_OUT, Collections.emptyList());
                this.appendBuyInOrSellOutData(logExtVoList);
                list.addAll(logExtVoList);
            }
            //转出至资金账户
            if (map.containsKey(BbAccountLogConst.CHANGE_OUT_FUND_ACCOUNT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.CHANGE_OUT_FUND_ACCOUNT, Collections.emptyList());
                this.appendFundsTransferData(logExtVoList);
                list.addAll(logExtVoList);
            }
            //转出至合约账户
            if (map.containsKey(BbAccountLogConst.CHANGE_OUT_PC_ACCOUNT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.CHANGE_OUT_PC_ACCOUNT, Collections.emptyList());
                this.appendFundsTransferData(logExtVoList);
                list.addAll(logExtVoList);
            }
            //资金账户转入
            if (map.containsKey(BbAccountLogConst.CHANGE_INTO_FUND_ACCOUNT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.CHANGE_INTO_FUND_ACCOUNT, Collections.emptyList());
                this.appendFundsTransferData(logExtVoList);
                list.addAll(logExtVoList);
            }
            //永续合约转入
            if (map.containsKey(BbAccountLogConst.CHANGE_INTO_PC_ACCOUNT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.CHANGE_INTO_PC_ACCOUNT, Collections.emptyList());
                this.appendFundsTransferData(logExtVoList);
                list.addAll(logExtVoList);
            }
        }
        return list;
    }

    private List<String> buildSymbols(String asset) {
        HashOperations opsForHash = templateDB0.opsForHash();
        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(BbExtRedisKey.BB_SYMBOL, ScanOptions.NONE);

        List<BBSymbol> list = new ArrayList<>();
        while (curosr.hasNext()) {
            Map.Entry<String, Object> entry = curosr.next();
            Object o = entry.getValue();
            BBSymbol bBSymbolVO = JSON.parseObject(o.toString(), BBSymbol.class);
            list.add(bBSymbolVO);
        }
         List<String> symbolList = list.stream().filter(symbol -> symbol.getAsset().equals(asset)).map(BBSymbol::getSymbol).collect(Collectors.toList());

        return symbolList;
    }

    //资金划转
    private void appendFundsTransferData(List<BbAccountLogExtVo> logExtVoList) {
        if (CollectionUtils.isEmpty(logExtVoList)) {
            return;
        }
        //refIds bb_account_record表的主键集合
        List<Long> refIds = logExtVoList.stream().map(BbAccountLogExtVo::getRefId).collect(Collectors.toList());
        List<BbAccountRecordVo> recordVos = bbAccountRecordExtService.queryByIds(refIds);
        if (!CollectionUtils.isEmpty(recordVos)) {
            Map<Long, BbAccountRecordVo> id2Vo = recordVos.stream().collect(Collectors.toMap(BbAccountRecordVo::getId, Function.identity()));
            for (BbAccountLogExtVo bbAccountLogExtVo : logExtVoList) {
                BbAccountRecordVo recordVo = id2Vo.get(bbAccountLogExtVo.getRefId());
                Optional<BbAccountRecordVo> recordOptional = Optional.ofNullable(recordVo);
                bbAccountLogExtVo.setVolume(recordOptional.map(BbAccountRecordVo::getAmount).orElse(BigDecimal.ZERO));
                bbAccountLogExtVo.setBalance(recordOptional.map(BbAccountRecordVo::getBalance).orElse(BigDecimal.ZERO));
                //资金划转没有手续费
                bbAccountLogExtVo.setFee(BigDecimal.ZERO);
                bbAccountLogExtVo.setTradeTime(recordOptional.map(BbAccountRecordVo::getCreated).orElse(null));
            }
        }
    }


    private void appendBuyInOrSellOutData(List<BbAccountLogExtVo> logExtVoList) {
        if (CollectionUtils.isEmpty(logExtVoList)) {
            return;
        }
        //refIds order_trade表的主键集合
        List<Long> refIds = logExtVoList.stream().map(BbAccountLogExtVo::getRefId).collect(Collectors.toList());
        List<BbOrderTradeVo> orderTradeVos = bbOrderTradeExtService.queryByIds(refIds);

        if (!CollectionUtils.isEmpty(orderTradeVos)) {
            Map<Long, BbOrderTradeVo> id2Vo = orderTradeVos.stream().collect(Collectors.toMap(BbOrderTradeVo::getId, Function.identity()));
            for (BbAccountLogExtVo bbAccountLogExtVo : logExtVoList) {
                BbOrderTradeVo bbOrderVo = id2Vo.get(bbAccountLogExtVo.getRefId());
                Optional<BbOrderTradeVo> bbOrderOptional = Optional.ofNullable(bbOrderVo);
                bbAccountLogExtVo.setFee(bbOrderOptional.map(BbOrderTradeVo::getFee).orElse(BigDecimal.ZERO));
                bbAccountLogExtVo.setVolume(bbOrderOptional.map(BbOrderTradeVo::getVolume).orElse(BigDecimal.ZERO));
                bbAccountLogExtVo.setTradeTime(bbOrderOptional.map(BbOrderTradeVo::getTradeTime).orElse(null));
                // TODO 买入卖出余额字段写死 ，后面优化
                bbAccountLogExtVo.setBalance(BigDecimal.ZERO);
            }
        }
    }

    private void checkParam(Long userId, String asset, Integer historyType, Integer tradeType, Long startDate,
                            Long endDate, Integer pageNo, Integer pageSize) {
        if (StringUtils.isEmpty(asset) || tradeType == null || null == userId ||
                pageNo == null || pageSize == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        if (BbextendConst.HISTORY_TYPE_LAST_THREE_MONTHS.equals(historyType)) {
            if (startDate == null || endDate == null) {
                throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
            }
        }


    }
}
