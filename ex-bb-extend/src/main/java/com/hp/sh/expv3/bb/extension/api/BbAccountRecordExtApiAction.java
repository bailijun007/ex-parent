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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@RestController
public class BbAccountRecordExtApiAction implements BbAccountRecordExtApi {

    private static final Logger logger = LoggerFactory.getLogger(BbAccountRecordExtApiAction.class);

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
        logger.info("userId={},asset={},historyType={},tradeType={},startDate={},endDate={},nextPage={},lastId={},pageSize={}", userId, asset, historyType, tradeType, startDate, endDate, nextPage, lastId, pageSize);
        List<BbAccountRecordExtVo> voList = null;
        try {
            if (null == lastId) {
                voList = bbAccountRecordExtService.listBbAccountRecords(userId, asset, historyType, tradeType, startDate, endDate, pageSize);
            } else {
                voList = bbAccountRecordExtService.listBbAccountRecordsByPage(userId, asset, historyType, tradeType, lastId, nextPage, startDate, endDate, pageSize);
            }

            if (!CollectionUtils.isEmpty(voList)) {
                List<Long> refId = new ArrayList<>();
                for (BbAccountRecordExtVo recordExtVo : voList) {
                    if (recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_BUY_IN) || recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_OUT) ||
                            recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_INCOME) || recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_RELEASE)) {
                        String replaceAll = recordExtVo.getTradeNo().replaceAll("[A-Z]", "");
                        long parseLong = Long.parseLong(replaceAll);
                        refId.add(parseLong);
                    }
                }

//             Map<Long, List<BbAccountRecordExtVo>> map2 = voList.stream().collect(Collectors.groupingBy(t -> Long.parseLong(t.getTradeNo().replaceAll("[A-Z]", ""))));
                //根据类型进行分组
                Map<Integer, List<BbAccountRecordExtVo>> map = voList.stream().collect(Collectors.groupingBy(BbAccountRecordExtVo::getTradeType));
                List<BbOrderTradeVo> bbOrderTradeVoList = bbOrderTradeExtService.queryByIds(refId);
                if (map.containsKey(BbAccountRecordConst.TRADE_BUY_IN) || map.containsKey(BbAccountRecordConst.TRADE_SELL_OUT) ||
                        map.containsKey(BbAccountRecordConst.TRADE_SELL_INCOME) || map.containsKey(BbAccountRecordConst.TRADE_SELL_RELEASE)) {
                    if (!CollectionUtils.isEmpty(bbOrderTradeVoList)) {
                        Map<Long, BbOrderTradeVo> orderTradeids2Map = bbOrderTradeVoList.stream().collect(Collectors.toMap(BbOrderTradeVo::getId, Function.identity()));
                        for (BbAccountRecordExtVo recordExtVo : voList) {
                            if (recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_BUY_IN) || recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_OUT) ||
                                    recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_INCOME) || recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_RELEASE)) {
                                long key = Long.parseLong(recordExtVo.getTradeNo().replaceAll("[A-Z]", ""));
                                if (orderTradeids2Map.containsKey(key)) {
                                    recordExtVo.setFee(orderTradeids2Map.get(key).getFee());
                                }
                                //做映射TradeType=9或者11 都属于买入；TradeType=10或者12 都属于卖出
                                if (recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_INCOME)) {
                                    recordExtVo.setTradeType(BbAccountRecordConst.TRADE_BUY_IN);
                                }
                                if (recordExtVo.getTradeType().equals(BbAccountRecordConst.TRADE_SELL_RELEASE)) {
                                    recordExtVo.setTradeType(BbAccountRecordConst.TRADE_SELL_OUT);
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("message={}", e.getMessage());
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
