package com.hp.sh.expv3.bb.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.bb.extension.constant.BbAccountRecordConst;
import com.hp.sh.expv3.bb.extension.constant.BbextendConst;
import com.hp.sh.expv3.bb.extension.dao.BbAccountRecordExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbAccountRecordExtServiceImpl implements BbAccountRecordExtService {

    @Autowired
    private BbAccountRecordExtMapper bbAccountRecordExtMapper;


    @Override
    public PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<BbAccountRecordVo> pageResult=new PageResult<>();
        PageHelper.startPage(pageNo,pageSize);
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        List<BbAccountRecordVo> list = bbAccountRecordExtMapper.queryList(map);
         PageInfo<BbAccountRecordVo> info = new PageInfo<BbAccountRecordVo>(list);
        pageResult.setList(list);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
         return pageResult;
    }

    @Override
    public List<BbAccountRecordVo> queryByIds(List<Long> refIds) {
        return bbAccountRecordExtMapper.queryByIds(refIds);

    }

    @Override
    public List<BbAccountRecordExtVo> listBbAccountRecords(Long userId, String asset, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer pageSize) {
        List<BbAccountRecordExtVo> list = null;
        Map<String, Object> map = new HashMap<>();
        simpleMap(userId, asset,  historyType, startDate, endDate, pageSize, map);
        if (BbextendConst.TRADE_TYPE_ALL.equals(tradeType)) {
            map.put("tradeTypes", BbAccountRecordConst.ALL_TRADE_TYPE);
            list = bbAccountRecordExtMapper.queryByLimit(map);
        } else {
            //10:买入，9：卖出
            if(tradeType==10){

            }
            map.put("tradeTypes", tradeType);
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
            list = bbAccountRecordExtMapper.listBbAccountRecordsByPage(map);
        } else {
            map.put("tradeType", tradeType);
            list = bbAccountRecordExtMapper.listBbAccountRecordsByPage(map);
        }
        return list;
    }


        private void simpleMap(Long userId, String asset, Integer historyType, Long startDate, Long endDate, Integer pageSize, Map<String, Object> map) {
        LocalDateTime localDateTime = LocalDateTime.now();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("limit", pageSize);
        try {
            if (BbextendConst.HISTORY_TYPE_LAST_TWO_DAYS.equals(historyType)) {
                LocalDateTime minusDays = localDateTime.minusDays(2L);
                long timeBegin = minusDays.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
                map.put("createdBegin", timeBegin);
            } else if (BbextendConst.HISTORY_TYPE_LAST_THREE_MONTHS.equals(historyType)) {
                map.put("createdBegin", startDate);
                map.put("createdEnd", endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
