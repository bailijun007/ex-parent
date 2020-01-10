package com.hp.sh.expv3.pc.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.dao.PcAccountLogDAO;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcAccountLogExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcAccountLogExtendServiceImpl implements PcAccountLogExtendService {
    @Autowired
    private PcAccountLogDAO pcAccountLogDAO;


    @Override
    public PageResult<PcAccountLogVo> getPcAccountLogList(Long userId, String asset, Integer tradeType, Integer historyType, Long startDate, Long endDate, String symbol, Integer pageNo, Integer pageSize) {
        PageResult<PcAccountLogVo> result=new PageResult<>();
        LocalDateTime localDateTime = LocalDateTime.now();
        PageHelper.startPage(pageNo,pageSize);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("type", tradeType);
        map.put("symbol", symbol);
        try {
            if (historyType == 1) {
                LocalDateTime minusDays = localDateTime.minusDays(2L);
                long timeBegin = minusDays.toInstant(ZoneOffset.ofHours(8)).toEpochMilli();
                map.put("timeBegin", timeBegin);
            } else if (historyType == 2) {
                map.put("timeBegin", startDate);
                map.put("timeEnd", endDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<PcAccountLogVo> list = pcAccountLogDAO.queryList(map);
        PageInfo<PcAccountLogVo> info = new PageInfo<>(list);
        result.setList(list);
        result.setRowTotal(info.getTotal());
        result.setPageNo(info.getPageNum());
        result.setPageCount(info.getPages());
        return result;
    }

    @Override
    public void save(PcAccountLogVo pcAccountLogVo) {
        int count = pcAccountLogDAO.save(pcAccountLogVo);
    }

    @Override
    public PcAccountLogVo getPcAccountLog(PcAccountLogVo pcAccountLogVo) {
        Map<String, Object> map=new HashMap<>();
        map.put("type", pcAccountLogVo.getType());
        map.put("refId", pcAccountLogVo.getRefId());
        map.put("userId", pcAccountLogVo.getUserId());
        map.put("asset", pcAccountLogVo.getAsset());
        map.put("symbol", pcAccountLogVo.getSymbol());
        PcAccountLogVo vo = pcAccountLogDAO.queryOne(map);
        return vo;
    }
}
