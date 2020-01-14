package com.hp.sh.expv3.pc.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.pc.extension.constant.ExtCommonConstant;
import com.hp.sh.expv3.pc.extension.dao.PcAccountLogDAO;
import com.hp.sh.expv3.pc.extension.service.PcAccountLogExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;
import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcAccountLogExtendServiceImpl implements PcAccountLogExtendService {
    @Autowired
    private PcAccountLogDAO pcAccountLogDAO;


    @Override
    public PageResult<PcAccountLogVo> pageQueryPcAccountLogList(Long userId, String asset, Integer tradeType, Integer historyType, Long startDate, Long endDate, String symbol, Integer pageNo, Integer pageSize) {
        PageResult<PcAccountLogVo> result = new PageResult<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        try {
            if (ExtCommonConstant.HISTORY_TYPE_LAST_TWO_DAYS.equals(historyType)) {
                LocalDateTime minusDays = localDateTime.minusDays(2L);
                long timeBegin = minusDays.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
                map.put("timeBegin", timeBegin);
            } else if (ExtCommonConstant.HISTORY_TYPE_LAST_THREE_MONTHS.equals(historyType)) {
                map.put("timeBegin", startDate);
                map.put("timeEnd", endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (ExtCommonConstant.TRADE_TYPE_ALL.equals(tradeType)) {
            Long count = pcAccountLogDAO.queryCount(map);
            map.put("limit", count);
            List<PcAccountLogVo> list = pcAccountLogDAO.queryByLimit(map);
            rePage(pageNo, pageSize, result, count, list);
        } else if (ExtCommonConstant.TRADE_TYPE_MAP.containsKey(tradeType)) {
            List<Integer> typeList = ExtCommonConstant.TRADE_TYPE_MAP.get(tradeType);
            map.put("types", typeList);
            Long count = pcAccountLogDAO.queryCount(map);
            map.put("limit", count);
            List<PcAccountLogVo> list = pcAccountLogDAO.queryByLimit(map);
            rePage(pageNo, pageSize, result, count, list);
        } else {
            PageHelper.startPage(pageNo, pageSize);
            map.put("type", tradeType);
            List<PcAccountLogVo> list = pcAccountLogDAO.queryList(map);
            PageInfo<PcAccountLogVo> info = new PageInfo<>(list);
            result.setList(list);
            result.setRowTotal(info.getTotal());
            result.setPageNo(info.getPageNum());
            result.setPageCount(info.getPages());
        }

        return result;
    }

    /**
     * 重新手动分页
     * @param pageNo
     * @param pageSize
     * @param result
     * @param count
     * @param list
     */
    private void rePage(Integer pageNo, Integer pageSize, PageResult<PcAccountLogVo> result, Long count, List<PcAccountLogVo> list) {
         List<PcAccountLogVo> voList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
        result.setList(voList);
        result.setRowTotal(count);
        result.setPageNo(pageNo);
        Integer rowTotal = Integer.parseInt(String.valueOf(count));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
    }

    @Override
    public void save(PcAccountLogVo pcAccountLogVo) {
        int count = pcAccountLogDAO.save(pcAccountLogVo);
    }

    @Override
    public PcAccountLogVo getPcAccountLog(PcAccountLogVo pcAccountLogVo) {
        Map<String, Object> map = new HashMap<>();
        map.put("type", pcAccountLogVo.getType());
        map.put("refId", pcAccountLogVo.getRefId());
        map.put("userId", pcAccountLogVo.getUserId());
        map.put("asset", pcAccountLogVo.getAsset());
        map.put("symbol", pcAccountLogVo.getSymbol());
        PcAccountLogVo vo = pcAccountLogDAO.queryOne(map);
        return vo;
    }
}
