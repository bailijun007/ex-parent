package com.hp.sh.expv3.bb.extension.api;

import com.alibaba.fastjson.JSON;
import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.constant.BbAccountRecordConst;
import com.hp.sh.expv3.bb.extension.constant.BbExtCommonConstant;
import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.pojo.BBSymbol;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.util.CommonDateUtils;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.sun.org.apache.bcel.internal.generic.NEW;
import jdk.nashorn.internal.ir.Symbol;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@RestController
public class BbAccountRecordExtApiAction implements BbAccountRecordExtApi {

    private static final Logger logger = LoggerFactory.getLogger(BbAccountRecordExtApiAction.class);

    @Resource(name = "templateDB0")
    private StringRedisTemplate templateDB0;

    @Autowired
    private BbAccountRecordExtService bbAccountRecordExtService;

    @Autowired
    private BbOrderTradeExtService bbOrderTradeExtService;

    @Override
    public PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, Integer pageSize, Integer pageNo, Long startTime, Long endTime) {
        if (pageSize == null || pageNo == null || StringUtils.isEmpty(asset)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        //如果开始时间，结束时间没有值则给默认今天时间
        Long[] startAndEndTime = CommonDateUtils.getStartAndEndTimeByLong(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        return bbAccountRecordExtService.queryHistory(userId, asset, startTime, endTime, pageNo, pageSize);
    }


    @Override
    public List<BbAccountRecordExtVo> query(Long userId, String asset, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer nextPage, Long lastId, Integer pageSize) {
        this.checkParam(userId, historyType, tradeType, startDate, endDate, nextPage, pageSize);
        logger.info("进入查询币币账单接口，收到参数为：userId={},asset={},historyType={},tradeType={},startDate={},endDate={},nextPage={},lastId={},pageSize={}", userId, asset, historyType, tradeType, startDate, endDate, nextPage, lastId, pageSize);
        List<BbAccountRecordExtVo> voList = null;
        try {
            if (BbextendConst.HISTORY_TYPE_LAST_TWO_DAYS.equals(historyType)) {
                 endDate = CommonDateUtils.getUTCTime();
                //一天等于多少毫秒：24*3600*1000
                long minusDay = (24 * 60 * 60 * 1000) * 2;
                startDate=endDate-minusDay;
            }

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
                 Set<String> defaultSymbols = getDefaultSymbols();
                ArrayList<String> symbols = new ArrayList<>(defaultSymbols);
                List<BbOrderTradeVo> bbOrderTradeVoList = bbOrderTradeExtService.queryByIds(refId,asset,symbols,startDate,endDate);
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
//            e.printStackTrace();
            logger.error("查询币币账单报错，报错message={},收到参数为：userId={},asset={},historyType={},tradeType={},startDate={},endDate={},nextPage={},lastId={},pageSize={}", e.getMessage(),userId, asset, historyType, tradeType, startDate, endDate, nextPage, lastId, pageSize);
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


    public Set<String> getDefaultSymbols() {
        HashOperations opsForHash = templateDB0.opsForHash();
        Cursor<Map.Entry<String, Object>> curosr = opsForHash.scan(BbExtCommonConstant.REDIS_KEY_BB_SYMBOL, ScanOptions.NONE);

        Set<String> list = new HashSet<>();
        while (curosr.hasNext()) {
            Map.Entry<String, Object> entry = curosr.next();
            Object o = entry.getValue();
            BBSymbol bBSymbolVO = JSON.parseObject(o.toString(), BBSymbol.class);
            list.add(bBSymbolVO.getSymbol());
        }
        return list;
    }
}
