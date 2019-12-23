package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcOrderDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor=Exception.class)
public class PcOrderExtendServiceImpl implements PcOrderExtendService {
    @Autowired
    private PcOrderDAO pcOrderDAO;

    @Override
    public BigDecimal getGrossMargin(Long userId, String asset) {
       if(userId==null){
           return BigDecimal.ZERO;
       }
        return pcOrderDAO.getGrossMargin(userId,asset);
    }

    @Override
    public  List<PcOrderVo> orderList(Long closePosId, Long userId) {
        Map<String, Object> map=new HashMap<>();
        map.put("closePosId",closePosId);
        map.put("userId",userId);
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryList(map);
        return pcOrderVos;
    }

    @Override
    public List<PcOrderVo> findCurrentUserOrder(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderType",orderType);
        map.put("longFlag",longFlag);
        map.put("closeFlag",closeFlag);
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryList(map);
        return pcOrderVos;
    }

    @Override
    public List<PcOrderVo> queryHistory(Long userId, String asset, String symbol, Integer orderType, Integer longFlag, Integer closeFlag, Long lastOrderId, Integer currentPage, Integer pageSize, Integer nextPage) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderType",orderType);
        map.put("longFlag",longFlag);
        map.put("closeFlag",closeFlag);
        map.put("lastOrderId",lastOrderId);
        map.put("currentPage",currentPage);
        map.put("pageSize",pageSize);
        map.put("nextPage",nextPage);
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryHistory(map);
        return pcOrderVos;
    }

    @Override
    public List<PcOrderVo> queryAll(Long userId, String asset, String symbol, Integer status, Integer longFlag, Integer closeFlag) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderType",status);
        map.put("longFlag",longFlag);
        map.put("closeFlag",closeFlag);
        List<PcOrderVo> pcOrderVos = pcOrderDAO.queryList(map);
        return pcOrderVos;
    }


}
