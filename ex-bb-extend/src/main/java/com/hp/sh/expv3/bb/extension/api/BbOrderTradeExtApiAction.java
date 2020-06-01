package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.service.BbOrderTradeExtService;
import com.hp.sh.expv3.bb.extension.util.CommonDateUtils;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeDetailVo;
import com.hp.sh.expv3.bb.extension.vo.BbOrderTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.extension.vo.BbUserOrderTrade;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.dev.CrossDB;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/15
 */
@RestController
public class BbOrderTradeExtApiAction implements BbOrderTradeExtApi {

    private static final Logger logger = LoggerFactory.getLogger(BbOrderTradeExtApiAction.class);

    @Autowired
    private BbOrderTradeExtService bbOrderTradeExtService;


    /**
     * 查小于某个时间点的最大的一条记录
     *
     * @param asset    资产
     * @param symbol   合约交易品种
     * @param statTime 成交时间
     * @return
     */
//    @Override
//    public BbOrderTradeVo selectLessTimeTrade(Long userId, String asset, String symbol, Long statTime,Long endTime) {
//        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || statTime == null ) {
//            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
//        }
//        return bbOrderTradeExtService.selectLessTimeTrade(asset, symbol, statTime,endTime);
//    }

//    @Override
//    public List<BbOrderTradeVo> selectAllTradeListByUser(String asset, String symbol, Long userId) {
//        if (userId == null) {
//            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
//        }
//        return bbOrderTradeExtService.selectAllTradeListByUser(asset, symbol, userId);
//    }
    @Override
    public List<BbUserOrderTrade> selectTradeListByUserId(String asset, String symbol, Long userId, Long id, Long startTime, Long endTime) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || userId == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }

        if (startTime == null) {
            LocalDate localDate = LocalDate.now();
            startTime = CommonDateUtils.localDateToTimestamp(localDate);
            if (endTime == null) {
                endTime = Instant.now().toEpochMilli();
            }
        }
        List<BbUserOrderTrade> tradeVo = bbOrderTradeExtService.selectTradeListByUserId(asset, symbol, startTime, endTime, userId, id);
        return tradeVo;
    }

    @Override
    public List<BbOrderTradeDetailVo> selectBbFeeCollectByAccountId(String asset, String symbol, Long userId, Long statTime, Long endTime) {
        if (userId == null || statTime == null || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }

        List<BbOrderTradeDetailVo> result = bbOrderTradeExtService.selectPcFeeCollectByAccountId(asset, symbol, userId, statTime, endTime);
        return result;
    }

    @Override
    public List<BbOrderTradeDetailVo> queryHistory(Long userId, String asset, String symbol, Long lastTradeId, Integer nextPage, Integer pageSize, Long startTime, Long endTime) {
        logger.info("进入获取当前用户交易明细接口，参数为：userId={},asset={},symbol={},lastTradeId={},nextPage={},pageSize={},startTime={},endTime={}", userId, asset, symbol, lastTradeId, nextPage, pageSize, startTime, endTime);
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || null == userId || pageSize == null || nextPage > 1 || nextPage < -1) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }

        Long[] startAndEndTime = CommonDateUtils.getStartAndEndTimeByLong(startTime, endTime);
        startTime = startAndEndTime[0];
        endTime = startAndEndTime[1];
        List<BbOrderTradeDetailVo> list = bbOrderTradeExtService.queryHistory(userId, asset, symbol, lastTradeId, nextPage, pageSize, startTime, endTime);
        if (CollectionUtils.isEmpty(list)) {
            return Lists.emptyList();
        }

        return list;
    }
}
