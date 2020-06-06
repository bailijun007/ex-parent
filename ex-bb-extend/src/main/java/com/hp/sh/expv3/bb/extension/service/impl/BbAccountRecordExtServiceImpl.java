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
        List<BbAccountRecordExtVo> list = null;
        Map<String, Object> map = new HashMap<>();
        simpleMap(userId, asset, historyType, startDate, endDate, pageSize, map);
        if (BbextendConst.TRADE_TYPE_ALL.equals(tradeType)) {
            map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_TYPE);
//            findBbAccountRecords(asset, startDate, endDate, list, map);
            list =bbAccountRecordExtMapper.queryByLimit(map);
        } else {
            //9:买入，10：卖出
            if (tradeType.equals(BbAccountRecordConst.TRADE_BUY_IN)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_BUY_IN);
            } else if (tradeType.equals(BbAccountRecordConst.TRADE_SELL_OUT)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_SELL_OUT);
            }
//            findBbAccountRecords(asset, startDate, endDate, list, map);
            list = bbAccountRecordExtMapper.queryByLimit(map);
        }
        return list;
    }

    @Override
    public List<BbAccountRecordExtVo> listBbAccountRecordsByPage(Long userId, String asset, Integer historyType, Integer tradeType, Long lastId, Integer nextPage, Long startDate, Long endDate, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        simpleMap(userId, asset, historyType, startDate, endDate, pageSize, map);
        map.put("lastId", lastId);
        map.put("nextPage", nextPage);
        List<BbAccountRecordExtVo> list = null;
        if (BbextendConst.TRADE_TYPE_ALL.equals(tradeType)) {
            map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_TYPE);
//            findBbAccountRecords(asset, startDate, endDate, list, map);
            list = bbAccountRecordExtMapper.listBbAccountRecordsByPage(map);
        } else {
            if (tradeType.equals(BbAccountRecordConst.TRADE_BUY_IN)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_BUY_IN);
            } else if (tradeType.equals(BbAccountRecordConst.TRADE_SELL_OUT)) {
                map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_SELL_OUT);
            }
//            findBbAccountRecords(asset, startDate, endDate, list, map);
          list = bbAccountRecordExtMapper.listBbAccountRecordsByPage(map);
        }
        return list;
    }

    @Override
    public PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, Long startTime, Long endTime, Integer pageNo, Integer pageSize) {
        PageResult<BbAccountRecordVo> pageResult = new PageResult<>();

//        long begin = CommonDateUtils.stringToTimestamp(startTime);
//        long end = CommonDateUtils.stringToTimestamp(endTime);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("createdBegin", startTime);
        map.put("createdEnd", endTime);

        Long count=bbAccountRecordExtMapper.queryCount(map);
        map.put("offset", pageNo-1);
        map.put("limit", pageSize);
        List<BbAccountRecordVo>  list = bbAccountRecordExtMapper.queryList(map);
        Integer rowTotal =count.intValue();
        pageResult.setList(list);
        pageResult.setPageNo(pageNo);
        pageResult.setRowTotal(count);
        pageResult.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return pageResult;
    }


    private void simpleMap(Long userId, String asset, Integer historyType, Long startDate, Long endDate, Integer pageSize, Map<String, Object> map) {
        LocalDate localDate = LocalDate.now();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("limit", pageSize);
        map.put("createdBegin", startDate);
        map.put("createdEnd", endDate);
//        try {
//            if (BbextendConst.HISTORY_TYPE_LAST_TWO_DAYS.equals(historyType)) {
//                LocalDate minusDays = localDate.minusDays(2L);
//                long timeBegin = CommonDateUtils.localDateToTimestamp(minusDays);
//                map.put("createdBegin", timeBegin);
//                map.put("createdEnd", CommonDateUtils.localDateToTimestamp(localDate));
//            } else if (BbextendConst.HISTORY_TYPE_LAST_THREE_MONTHS.equals(historyType)) {
//                map.put("createdBegin", startDate);
//                map.put("createdEnd", endDate);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
