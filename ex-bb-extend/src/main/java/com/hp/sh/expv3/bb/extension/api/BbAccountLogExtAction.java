package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.constant.BbAccountLogConst;
import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbAccountLogExtService;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

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
    private BbOrderExtService bbOrderExtService;

    @Autowired
    private BbAccountRecordExtService bbAccountRecordExtService;

    @Override
    public List<BbAccountLogExtVo> query(Long userId, String asset, String symbol, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer nextPage, Integer lastId, Integer pageSize) {
        List<BbAccountLogExtVo> list = new ArrayList<>();
        this.checkParam(userId, asset, symbol, historyType, tradeType, startDate, endDate, nextPage, pageSize);
        List<BbAccountLogExtVo> voList = null;
        if (null == lastId) {
            voList = bbAccountLogExtService.listBbAccountLogs(userId, asset, symbol, historyType, tradeType, startDate, endDate, pageSize);
        } else {
            voList = bbAccountLogExtService.listBbAccountLogsByPage(userId, asset, symbol, historyType, tradeType, lastId, nextPage, startDate, endDate, pageSize);
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
                this.appendChangeToFundAcountData(logExtVoList);
                list.addAll(logExtVoList);
            }
            //转出至合约账户
            if (map.containsKey(BbAccountLogConst.CHANGE_OUT_PC_ACCOUNT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.CHANGE_OUT_PC_ACCOUNT, Collections.emptyList());
                //refIds 其他关联表的主键集合
                List<Long> refIds = logExtVoList.stream().map(BbAccountLogExtVo::getRefId).collect(Collectors.toList());
                this.appendChangeToPcAcountData(asset, symbol, userId, refIds);
                list.addAll(logExtVoList);
            }
            //资金账户转入
            if (map.containsKey(BbAccountLogConst.CHANGE_INTO_FUND_ACCOUNT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.CHANGE_INTO_FUND_ACCOUNT, Collections.emptyList());
                //refIds 其他关联表的主键集合
                List<Long> refIds = logExtVoList.stream().map(BbAccountLogExtVo::getRefId).collect(Collectors.toList());
                this.appendIntoFundAcountData(asset, symbol, userId, refIds);
                list.addAll(logExtVoList);
            }
            //永续合约转入
            if (map.containsKey(BbAccountLogConst.CHANGE_INTO_PC_ACCOUNT)) {
                List<BbAccountLogExtVo> logExtVoList = map.getOrDefault(BbAccountLogConst.CHANGE_INTO_PC_ACCOUNT, Collections.emptyList());
                //refIds 其他关联表的主键集合
                List<Long> refIds = logExtVoList.stream().map(BbAccountLogExtVo::getRefId).collect(Collectors.toList());
                this.appendIntoPcAcountData(asset, symbol, userId, refIds);
                list.addAll(logExtVoList);
            }

        }
        return list;
    }

    private void appendIntoPcAcountData(String asset, String symbol, Long userId, List<Long> refIds) {

    }

    private void appendIntoFundAcountData(String asset, String symbol, Long userId, List<Long> refIds) {

    }

    private void appendChangeToPcAcountData(String asset, String symbol, Long userId, List<Long> refIds) {

    }

    private void appendChangeToFundAcountData(List<BbAccountLogExtVo> logExtVoList) {
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
                bbAccountLogExtVo.setFee(BigDecimal.ZERO);
            }

        }


    }

    private void appendSellOutData(String asset, String symbol, Long userId, List<Long> refIds) {

    }

    private void appendBuyInOrSellOutData(List<BbAccountLogExtVo> logExtVoList) {
        //refIds order表的主键集合
        List<Long> refIds = logExtVoList.stream().map(BbAccountLogExtVo::getRefId).collect(Collectors.toList());
        List<BbOrderVo> bbOrderVos = bbOrderExtService.queryByIds(refIds);
        if (!CollectionUtils.isEmpty(bbOrderVos)) {
            Map<Long, BbOrderVo> id2Vo = bbOrderVos.stream().collect(Collectors.toMap(BbOrderVo::getId, Function.identity()));
            for (BbAccountLogExtVo bbAccountLogExtVo : logExtVoList) {
                BbOrderVo bbOrderVo = id2Vo.get(bbAccountLogExtVo.getRefId());
                Optional<BbOrderVo> bbOrderOptional = Optional.ofNullable(bbOrderVo);
                bbAccountLogExtVo.setFee(bbOrderOptional.map(BbOrderVo::getFeeCost).orElse(BigDecimal.ZERO));
                bbAccountLogExtVo.setVolume(bbOrderOptional.map(BbOrderVo::getVolume).orElse(BigDecimal.ZERO));
            }
        }
    }

    private void checkParam(Long userId, String asset, String symbol, Integer historyType, Integer tradeType, Long startDate,
                            Long endDate, Integer pageNo, Integer pageSize) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || tradeType == null || null == userId ||
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
