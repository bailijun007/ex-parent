package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.constant.BbAccountRecordConst;
import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@RestController
public class BbAccountRecordExtApiAction implements BbAccountRecordExtApi {

    @Autowired
    private BbAccountRecordExtService bbAccountRecordExtService;

    @Autowired
    private BbOrderTradeExtService bbOrderTradeExtService;

    @Override
    public PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, Integer pageSize, Integer pageNo) {
        if (pageSize == null || pageNo == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbAccountRecordExtService.queryHistory(userId, asset, pageNo, pageSize);
    }

    @Override
    public List<BbAccountRecordExtVo> query(Long userId, String asset, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer nextPage, Long lastId, Integer pageSize) {
        this.checkParam(userId, historyType, tradeType, startDate, endDate, nextPage, pageSize);
        List<BbAccountRecordExtVo> voList = null;
        if (null == lastId) {
            voList = bbAccountRecordExtService.listBbAccountRecords(userId, asset, historyType, tradeType, startDate, endDate, pageSize);
        } else {
            voList = bbAccountRecordExtService.listBbAccountRecordsByPage(userId, asset, historyType, tradeType, lastId, nextPage, startDate, endDate, pageSize);
        }

        if (!CollectionUtils.isEmpty(voList)) {
            if (tradeType == BbAccountRecordConst.ACCOUNT_FUND_TO_BB || tradeType == BbAccountRecordConst.ACCOUNT_BB_TO_FUND ||
                    tradeType == BbAccountRecordConst.ACCOUNT_PC_TO_BB || tradeType == BbAccountRecordConst.ACCOUNT_BB_TO_PC) {
                List<Long> refId = voList.stream().map(BbAccountRecordExtVo::getAssociatedId).collect(Collectors.toList());
                List<BbOrderTradeVo> bbOrderTradeVoList = bbOrderTradeExtService.queryByIds(refId);
                if (!CollectionUtils.isEmpty(bbOrderTradeVoList)) {
                    Map<Long, BbOrderTradeVo> id2Vo = bbOrderTradeVoList.stream().collect(Collectors.toMap(BbOrderTradeVo::getId, Function.identity()));
                    for (BbAccountRecordExtVo recordExtVo : voList) {
                        if (id2Vo.containsKey(recordExtVo.getAssociatedId())) {
                            recordExtVo.setFee(id2Vo.get(recordExtVo.getAssociatedId()).getFee());
                        }
                    }
                }
            }
            for (BbAccountRecordExtVo recordExtVo : voList) {
                recordExtVo.setFee(recordExtVo.getFee() == null ? BigDecimal.ZERO : recordExtVo.getFee());
            }
        }

        return voList;
    }


    private void checkParam(Long userId, Integer historyType, Integer tradeType, Long startDate,
                            Long endDate, Integer pageNo, Integer pageSize) {
        if (tradeType == null || null == userId ||
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
