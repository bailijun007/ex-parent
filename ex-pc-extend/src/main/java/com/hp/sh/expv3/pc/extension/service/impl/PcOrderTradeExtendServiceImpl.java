package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcOrderTradeDAO;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcLiqRecordVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/23
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcOrderTradeExtendServiceImpl implements PcOrderTradeExtendService {
    @Autowired
    private PcOrderTradeDAO pcOrderTradeDAO;

    @Override
    public BigDecimal getRealisedPnl(Long posId, Long userId) {
        return pcOrderTradeDAO.getRealisedPnl(posId,userId,null);
    }

    @Override
    public List<PcOrderTradeVo> queryOrderTrade(Long userId, String asset, String symbol, String orderId) {
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderId",orderId);
        List<PcOrderTradeVo> voList = pcOrderTradeDAO.queryList(map);
        return voList;
    }

    @Override
    public PcOrderTradeVo getPcOrderTrade(Long refId, String asset, String symbol, Long userId, Long time) {
        Map<String, Object> map=new HashMap<>();
        map.put("id",refId);
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("userId",userId);
        String start = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(time));
        map.put("createdBegin",start);
        PcOrderTradeVo pcOrderTradeVo = pcOrderTradeDAO.queryOne(map);
        return pcOrderTradeVo;
    }
}
