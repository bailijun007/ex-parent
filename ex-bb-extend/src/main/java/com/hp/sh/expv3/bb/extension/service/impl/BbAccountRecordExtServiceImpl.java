package com.hp.sh.expv3.bb.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.constant.BbAccountRecordConst;
import com.hp.sh.expv3.bb.extension.constant.BbExtCommonConstant;
import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.dao.BbAccountRecordExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.util.CommonDateUtils;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbAccountRecordExtServiceImpl implements BbAccountRecordExtService {

    @Autowired
    private BbAccountRecordExtMapper bbAccountRecordExtMapper;


    @Override
    public List<BbAccountRecordVo> queryByIds(List<Long> refIds) {
        return bbAccountRecordExtMapper.queryByIds(refIds);

    }

    @Override
    public List<BbAccountRecordExtVo> listBbAccountRecords(Long userId, String asset, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer pageSize) {
        List<BbAccountRecordExtVo> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        simpleMap(userId, asset, historyType, startDate, endDate, pageSize, map);
        if (BbextendConst.TRADE_TYPE_ALL.equals(tradeType)) {
            map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_TYPE);
            findBbAccountRecords(asset, startDate, endDate, list, map);

        } else {
            //9:买入，10：卖出
            if (tradeType.equals(BbAccountRecordConst.TRADE_BUY_IN)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_BUY_IN);
            } else if (tradeType.equals(BbAccountRecordConst.TRADE_SELL_OUT)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_SELL_OUT);
            }
            findBbAccountRecords(asset, startDate, endDate, list, map);
//            list = bbAccountRecordExtMapper.queryByLimit(map);
        }
        return list;
    }

    private void findBbAccountRecords(String asset, Long startDate, Long endDate, List<BbAccountRecordExtVo> list, Map<String, Object> map) {
        String startTime = CommonDateUtils.timestampToString(startDate);
        String endTime = CommonDateUtils.timestampToString(endDate);
        List<Integer> timeDifference = CommonDateUtils.getTimeDifference(startTime, endTime);
        String tableName = null;
        for (Integer date : timeDifference) {
            //tableName  eg: bb_account_record_btc__202005
            tableName = BbExtCommonConstant.BB_ACCOUNT_RECORD_PREFIX + asset.toLowerCase() + "__" + date;
            map.put("tableName", tableName);

            int existTable = bbAccountRecordExtMapper.existTable(BbExtCommonConstant.DB_NAME_EXPV3_BB, tableName);
            //表不存在则直接跳过
            if (existTable != 1) {
                continue;
            }
             List<BbAccountRecordExtVo> recordExtVos = bbAccountRecordExtMapper.queryByLimit(map);
            if(!CollectionUtils.isEmpty(recordExtVos)){
                list.addAll(recordExtVos);
            }

        }
    }

    @Override
    public List<BbAccountRecordExtVo> listBbAccountRecordsByPage(Long userId, String asset, Integer historyType, Integer tradeType, Long lastId, Integer nextPage, Long startDate, Long endDate, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        simpleMap(userId, asset, historyType, startDate, endDate, pageSize, map);
        map.put("lastId", lastId);
        map.put("nextPage", nextPage);
        List<BbAccountRecordExtVo> list = new ArrayList<>();
        if (BbextendConst.TRADE_TYPE_ALL.equals(tradeType)) {
            map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_TYPE);
            findBbAccountRecords(asset, startDate, endDate, list, map);
//            list = bbAccountRecordExtMapper.listBbAccountRecordsByPage(map);
        } else {
            if (tradeType.equals(BbAccountRecordConst.TRADE_BUY_IN)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_BUY_IN);
            } else if (tradeType.equals(BbAccountRecordConst.TRADE_SELL_OUT)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_SELL_OUT);
            }
            findBbAccountRecords(asset, startDate, endDate, list, map);
//            list = bbAccountRecordExtMapper.listBbAccountRecordsByPage(map);
        }
        return list;
    }

    @Override
    public PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, String startTime, String endTime, Integer pageNo, Integer pageSize) {
        PageResult<BbAccountRecordVo> pageResult = new PageResult<>();
        List<BbAccountRecordVo> list = new ArrayList<>();
        long begin = CommonDateUtils.stringToTimestamp(startTime);
        long end = CommonDateUtils.stringToTimestamp(endTime);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("createdBegin", begin);
        map.put("createdEnd", end);
        List<Integer> timeDifference = CommonDateUtils.getTimeDifference(startTime, endTime);
        String tableName = null;

        for (Integer date : timeDifference) {
            tableName = BbExtCommonConstant.BB_ACCOUNT_RECORD_PREFIX + asset.toLowerCase() + "__" + date;
            int existTable = bbAccountRecordExtMapper.existTable(BbExtCommonConstant.DB_NAME_EXPV3_BB, tableName);
            //表不存在则直接跳过
            if (existTable != 1) {
                continue;
            }
            map.put("tableName", tableName);
            List<BbAccountRecordVo> bbAccountRecordVos = bbAccountRecordExtMapper.queryList(map);
            if(!CollectionUtils.isEmpty(bbAccountRecordVos)){
                list.addAll(bbAccountRecordVos);
            }

        }

        List<BbAccountRecordVo> pageList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
        pageResult.setList(pageList);
        Integer rowTotal = list.size();
        pageResult.setPageNo(pageNo);
        pageResult.setRowTotal(Long.parseLong(String.valueOf(rowTotal)));
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);

        return pageResult;
    }


    private void simpleMap(Long userId, String asset, Integer historyType, Long startDate, Long endDate, Integer pageSize, Map<String, Object> map) {
        LocalDateTime localDateTime = LocalDateTime.now();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("limit", pageSize);
        try {
            if (BbextendConst.HISTORY_TYPE_LAST_TWO_DAYS.equals(historyType)) {
                LocalDateTime minusDays = localDateTime.minusDays(2L);
                long timeBegin = CommonDateUtils.localDateTimeToTimestamp(minusDays);
                map.put("createdBegin", timeBegin);
                map.put("createdEnd", CommonDateUtils.localDateTimeToTimestamp(localDateTime));
            } else if (BbextendConst.HISTORY_TYPE_LAST_THREE_MONTHS.equals(historyType)) {
                map.put("createdBegin", startDate);
                map.put("createdEnd", endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
