package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.dao.BbAccountLogExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbAccountLogExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author BaiLiJun  on 2020/3/24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbAccountLogExtServiceImpl implements BbAccountLogExtService {

    @Autowired
    private BbAccountLogExtMapper bbAccountLogExtMapper;

    @Override
    public List<BbAccountLogExtVo> listBbAccountLogs(Long userId, String asset, List<String> symbols, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer pageSize) {
        List<BbAccountLogExtVo> list = null;
        Map<String, Object> map = new HashMap<>();
        simpleMap(userId, asset, symbols, historyType, startDate, endDate, pageSize, map);
        if (BbextendConst.TRADE_TYPE_ALL.equals(tradeType)) {
            list = bbAccountLogExtMapper.queryByLimit(map);
        } else {
            map.put("type", tradeType);
            list = bbAccountLogExtMapper.queryByLimit(map);
        }
        return list;
    }


    @Override
    public List<BbAccountLogExtVo> listBbAccountLogsByPage(Long userId, String asset, List<String> symbols, Integer historyType, Integer tradeType, Long lastId, Integer nextPage, Long startDate, Long endDate, Integer pageSize) {
        Map<String, Object> map = new HashMap<>();
        simpleMap(userId, asset, symbols, historyType, startDate, endDate, pageSize, map);
        map.put("lastId", lastId);
        map.put("nextPage", nextPage);
        List<BbAccountLogExtVo> list = null;
        if (BbextendConst.TRADE_TYPE_ALL.equals(tradeType)) {
            list = bbAccountLogExtMapper.listBbAccountLogsByPage(map);
        } else {
            map.put("type", tradeType);
            list = bbAccountLogExtMapper.listBbAccountLogsByPage(map);
        }
        return list;
    }


    private void simpleMap(Long userId, String asset,  List<String> symbols, Integer historyType, Long startDate, Long endDate, Integer pageSize, Map<String, Object> map) {
        LocalDateTime localDateTime = LocalDateTime.now();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("symbols", symbols);
        map.put("limit", pageSize);
        try {
            if (BbextendConst.HISTORY_TYPE_LAST_TWO_DAYS.equals(historyType)) {
                LocalDateTime minusDays = localDateTime.minusDays(2L);
                long timeBegin = minusDays.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
                map.put("timeBegin", timeBegin);
            } else if (BbextendConst.HISTORY_TYPE_LAST_THREE_MONTHS.equals(historyType)) {
                map.put("timeBegin", startDate);
                map.put("timeEnd", endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
