package com.hp.sh.expv3.bb.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import com.hp.sh.expv3.bb.extension.vo.BbHistoryOrderVo;
import com.hp.sh.expv3.commons.exception.ExException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@RestController
public class BbOrderExtApiAction implements BbOrderExtApi {

    private static final Logger logger = LoggerFactory.getLogger(BbOrderExtApiAction.class);

    @Autowired
    private BbOrderExtService bbOrderExtService;

    @Override
    public PageResult<BbOrderVo> queryAllBbOrederHistory(Long userId, String asset, String symbol, String startTime, String endTime, Integer pageSize, Integer pageNo) {
        if (pageSize == null || pageNo == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        startTime = getDefaultDateTime(startTime);
        endTime = getDefaultDateTime(endTime);
        return bbOrderExtService.queryAllBbOrederHistory(userId, asset, symbol, startTime, endTime, pageNo, pageSize);
    }


    @Override
    public PageResult<BbHistoryOrderVo> queryHistoryOrderList(Long userId, String asset, String symbol, Integer bidFlag, String startTime, String endTime, Integer pageSize, Long lastOrderId, Integer nextPage) {
        long start = System.currentTimeMillis();
        checkParam(userId, asset, symbol, pageSize, nextPage);
        startTime = getDefaultDateTime(startTime);
        endTime = getDefaultDateTime(endTime);
        PageResult<BbHistoryOrderVo> result = bbOrderExtService.queryHistoryOrderList(userId, asset, symbol, bidFlag, pageSize, lastOrderId, nextPage, startTime, endTime);
        if (result != null) {
            long end = System.currentTimeMillis();
            logger.info("查询历史委托接口耗时：{}毫秒,userId={},asset={},symbol={}", (end - start), asset, symbol);
        }
        return result;
    }


    @Override
    public PageResult<BbHistoryOrderVo> queryBbActiveOrderList(Long userId, String asset, String symbol, Integer bidFlag, Integer pageSize, Long lastOrderId, Integer nextPage) {
        if (userId == null || StringUtils.isEmpty(asset) || pageSize == null || nextPage == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbOrderExtService.queryBbActiveOrderList(userId, asset, symbol, bidFlag, pageSize, lastOrderId, nextPage);
    }

    @Override
    public List<BbHistoryOrderVo> queryOrderList(Long userId, String asset, String symbol, Long gtOrderId, Long ltOrderId, Integer count, String status, String startTime, String endTime) {
        if (null == userId || count == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        //如果同时传了gtOrderId和ltOrderId 则以gtOrderId为查询条件，同时不传，则查全部
        if (gtOrderId != null && ltOrderId != null) {
            ltOrderId = null;
        }

        List<String> assetList = null;
        List<String> symbolList = null;
        List<Integer> statusList = null;
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(asset)) {
            assetList = Arrays.asList(asset.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(symbol)) {
            symbolList = Arrays.asList(symbol.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        }
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(status)) {
            statusList = Arrays.asList(status.split(",")).stream().map(s -> Integer.parseInt(s.trim())).collect(Collectors.toList());
        }
        List<BbHistoryOrderVo> list = bbOrderExtService.queryOrderList(userId, assetList, symbolList, gtOrderId, ltOrderId, count, statusList, startTime, endTime);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }

        return list;
    }

    @Override
    public BigDecimal queryTotalFee(String asset, String symbol, Long startTime, Long endTime) {
        if (null == startTime || endTime == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbOrderExtService.queryTotalFee(asset, symbol, startTime, endTime);
    }

    @Override
    public BigDecimal queryTotalOrder(String asset, String symbol, Long startTime, Long endTime) {
        if (null == startTime || endTime == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        return bbOrderExtService.queryTotalOrder(asset, symbol, startTime, endTime);
    }


    private void checkParam(Long userId, String asset, String symbol, Integer pageSize, Integer nextPage) {
        if (userId == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || pageSize == null || nextPage == null) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
    }

    private String getDefaultDateTime(String startTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime dateTime = LocalDateTime.now();
        //如果开始时间，结束时间没有值则给默认今天时间
        if (StringUtils.isEmpty(startTime)) {
            startTime = formatter.format(dateTime);
        }
        return startTime;
    }

}
